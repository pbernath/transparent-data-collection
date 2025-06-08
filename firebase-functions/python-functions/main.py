from datetime import datetime, timezone
from firebase_admin import initialize_app, firestore
from firebase_functions import https_fn
from google.cloud.firestore_v1 import Increment

initialize_app()

db = firestore.client(database_id='(default)')  # Initialize Firestore client with the specified database ID



@https_fn.on_call()
def getAvailableContracts(req: https_fn.CallableRequest):
    """
    Returns all contracts the authenticated user has not yet interacted with.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - contracts (List[Dict]): A list of contract documents excluding those already interacted with.

    📝 Notes:
        - This fetches documents from the "contracts" collection, excluding those present in "interacted_contracts" for the user.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] getAvailableContracts called for user: {user_id}")

    try:
        contracts_ref = db.collection("contracts")
        interacted_ref = db.collection("interacted_contracts")
        user_ref = db.collection("users").document(user_id)

        all_contracts = list(contracts_ref.stream())
        interacted_docs = list(interacted_ref.where("user", "==", user_ref).stream())

        interacted_ids = {doc.get("contract").id for doc in interacted_docs if doc.get("contract")}
        filtered = [
            {**doc.to_dict(), "contract_id": doc.id}
            for doc in all_contracts if doc.id not in interacted_ids
        ]

        return {"contracts": filtered}

    except Exception as e:
        print(f"[ERROR] getAvailableContracts failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while fetching available contracts."
        )



@https_fn.on_call()
def getInteractedContracts(req: https_fn.CallableRequest):
    """
    Returns all contracts the user has interacted with, merged with the original contract data.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - contracts (List[Dict]): A list of contracts with interaction metadata and calculated statuses.

    📝 Notes:
        - Contract interaction data includes status such as "Finished", "Active", or "Awaiting activation".
        - If the contract has passed its end time, it is marked as finished using the helper.
        - Contract metadata is combined from both collections.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] getInteractedContracts called for user: {user_id}")

    try:
        interacted_ref = db.collection("interacted_contracts")
        user_ref = db.collection("users").document(user_id)
        docs = list(interacted_ref.where("user", "==", user_ref).stream())

        if not docs:
            return {"contracts": []}

        contract_refs = []
        interaction_map = {}

        for doc in docs:
            data = doc.to_dict()
            contract = data.get("contract")
            if isinstance(contract, firestore.DocumentReference):
                contract_refs.append(contract)
                data.pop("contract", None)
                data.pop("user", None)
                interaction_map[contract] = (doc.id, data)

        fetched_contracts = db.get_all(contract_refs)
        now = datetime.now(timezone.utc)
        results = []

        for contract_doc in fetched_contracts:
            if not contract_doc.exists:
                continue
            contract_data = contract_doc.to_dict()
            contract_ref = contract_doc.reference
            interacted_id, interaction_data = interaction_map[contract_ref]

            start = contract_data.get("start")
            end = contract_data.get("end")

            if end and end < now:
                if interaction_data.get("finished") is None:
                    # Mark as finished using the helper
                    stop_interacted_contract(
                        user_id,
                        interacted_id,
                        FINISH
                    )
                interaction_data["status"] = "Finished"
                interaction_data["finished"] = end
            elif start and start < now:
                interaction_data["status"] = "Active"
            else:
                interaction_data["status"] = "Awaiting activation"

            merged = {
                **interaction_data,
                **contract_data,
                "contract_id": contract_ref.id,
                "interacted_id": interacted_id
            }
            results.append(merged)

        return {"contracts": results}

    except Exception as e:
        print(f"[ERROR] getInteractedContracts failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while fetching interacted contracts."
        )



@https_fn.on_call()
def getUserInfo(req: https_fn.CallableRequest):
    """
    Fetches the authenticated user's profile data from Firestore.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - A dictionary containing all fields in the user's document, plus the user_id field.

    📝 Notes:
        - This reads the document from the "users" collection using the caller's UID.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] getUserInfo called for user: {user_id}")

    try:
        user_doc = db.collection("users").document(user_id).get()

        if not user_doc.exists:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="User information not found."
            )

        data = user_doc.to_dict()
        data["user_id"] = user_id
        return data

    except Exception as e:
        print(f"[ERROR] getUserInfo failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while fetching user information."
        )



# Action constants
ACCEPT = "accept"
IGNORE = "ignore"
INTERRUPT = "interrupt"
FINISH = "finish"

# Action groups
VALID_ACTIONS = {ACCEPT, IGNORE, INTERRUPT, FINISH}
EARLY_ACTIONS = {ACCEPT, IGNORE}
LATE_ACTIONS = {INTERRUPT, FINISH}



@https_fn.on_call()
def handleContractInteraction(req: https_fn.CallableRequest):
    """
    Handles contract interaction for the authenticated user.

    🔐 Authentication:
        - This function must be called by an authenticated user.

    📥 Expected Input (in req.data):
        - contractId (str): The document ID for the contract interaction.
            - If action is "accept" or "ignore": contractId refers to a document in the "contracts" collection.
            - If action is "interrupt" or "finish": contractId refers to a document in the "interacted_contracts" collection.
        - action (str): The type of interaction. One of:
            - "accept"    → User accepts the contract.
            - "ignore"    → User ignores the contract.
            - "interrupt" → User interrupts a previously accepted contract.
            - "finish"    → User finishes a previously accepted contract.

    📤 Output:
        - A JSON object with a "message" field indicating the result.

    📝 Notes:
        - For "accept" and "ignore", a new document is created in 'interacted_contracts'.
        - For "interrupt" and "finish", an existing document is updated (if found).
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user.",
        )

    user_id = req.auth.uid
    contract_id = req.data.get("contractId")
    action = req.data.get("action")

    print(f"[INFO] handleContractInteraction called for user: {user_id}, contract: {contract_id}, action: {action}")

    if not contract_id or not action:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
            message="Missing 'contractId' or 'action'.",
        )

    if action not in VALID_ACTIONS:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
            message=f"Invalid action. Must be one of {', '.join(VALID_ACTIONS)}.",
        )

    print(f"[INFO] User {user_id} is performing '{action}' on contract {contract_id}")

    if action in EARLY_ACTIONS:
        result = create_interacted_contract(user_id, contract_id, action)
    else:
        result = stop_interacted_contract(user_id, contract_id, action)

    return {"message": result["message"]}



def create_interacted_contract(user_id: str, contract_id: str, action: str) -> dict:
    """
    Creates a new document in 'interacted_contracts' to represent a user’s response to a contract.

    📥 Input:
        - contract_id (str): ID of the contract being interacted with.
        - user_id (str): ID of the user performing the interaction.
        - status (str): Interaction type (e.g., "accept" or "ignore").

    📤 Output:
        - None

    📝 Notes:
        - The new document ID is a combination of user_id and contract_id.
        - Timestamps and interaction status are recorded in Firestore.
    """
    print(f"[INFO] Helper function create_interacted_contract called for user: {user_id}, contract: {contract_id}, action: {action}")
    interacted_contracts_ref = db.collection("interacted_contracts")
    contract_ref = db.collection("contracts").document(contract_id)
    user_ref = db.collection("users").document(user_id)

    doc_data = {
        "contract": contract_ref,
        "user": user_ref,
        "created": firestore.SERVER_TIMESTAMP,
        "accepted": action == ACCEPT,
        "ignored": action == IGNORE,
        "interrupted": False,
        "finished": None,
        "payout": None
    }

    interacted_contracts_ref.add(doc_data)

    print(f"[INFO] Created interaction: user={user_id}, contract={contract_id}, action={action}")
    return {
        "message": f"Contract {contract_id} {action}ed successfully!"
    }



def stop_interacted_contract(user_id: str, interaction_id: str, action: str) -> dict:
    """
    Updates an existing interacted_contracts document to reflect that a user finished or interrupted it.

    📥 Input:
        - doc_ref: Firestore document reference to the contract interaction.
        - user_id (str): ID of the user updating the interaction.
        - action (str): Either "interrupt" or "finish".

    📤 Output:
        - None

    📝 Notes:
        - Sets the "ended_at" timestamp and updates the "status" field.
    """
    print(f"[INFO] Helper function stop_interacted_contract called for user: {user_id}, interaction: {interaction_id}, action: {action}")
    try:
        interacted_contract_ref = db.collection("interacted_contracts").document(interaction_id)
        interacted_contract_snapshot = interacted_contract_ref.get()

        if not interacted_contract_snapshot.exists:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message=f"No interaction found with ID {interaction_id}."
            )

        interacted_contract_data = interacted_contract_snapshot.to_dict()
        user_ref = interacted_contract_data.get("user")

        if not user_ref or user_ref.id != user_id:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.PERMISSION_DENIED,
                message=f"Interaction {interaction_id} does not belong to user {user_id}."
            )

        contract_ref = interacted_contract_data.get("contract")
        if not contract_ref:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="Contract reference not found in interaction document."
            )
        contract_snapshot = contract_ref.get()
        if not contract_snapshot.exists:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="Referenced contract document not found."
            )
        contract_data = contract_snapshot.to_dict()

        if action == INTERRUPT:
            contract_start = contract_data.get("start")
            current_time = datetime.now(timezone.utc)
            if contract_start and contract_start > current_time:
                interacted_contract_ref.delete()
                print(f"[INFO] Interaction {interaction_id} deleted due to future contract start date.")
                return {
                    "message": f"Interaction {interaction_id} deleted because contract hasn't started yet."
                }

            updates = {
                "interrupted": True,
                "finished": firestore.SERVER_TIMESTAMP,
                "payout": 0
            }

        elif action == FINISH:
            contract_value = contract_data.get("reward", 0)
            user_ref.update({"credits": Increment(contract_value)})

            updates = {
                "finished": firestore.SERVER_TIMESTAMP,
                "payout": contract_value
            }

            print(f"[INFO] User {user_id} finished contract {interaction_id}. Payout: {contract_value}")

        else:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="Invalid action. Must be 'interrupt' or 'finish'."
            )
        
        interacted_contract_ref.update(updates)
        print(f"[INFO] Updated interaction {interaction_id} for user {user_id} with action {action}")

        return {
            "message": f"Contract {interaction_id} {action}ed successfully!"
        }

    except https_fn.HttpsError as e:
        raise e
    except Exception as e:
        print(f"[ERROR] Unexpected error while stopping interacted contract: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred."
        )



@https_fn.on_call()
def requestUserDeletion(req: https_fn.CallableRequest):
    """
    Flags the user's Firestore document for account deletion.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - message (str): Confirmation of request processing.

    📝 Notes:
        - Updates the "deletion_requested" field in the user's document to True.
        - Often the first step in compliance-related workflows (e.g., GDPR).
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] requestUserDeletion called for user: {user_id}")

    try:
        user_doc_ref = db.collection("users").document(user_id)
        if not user_doc_ref.get().exists:
            print(f"[ERROR] User document not found for deletion request: {user_id}")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="User document does not exist."
            )

        user_doc_ref.update({"deletion_requested": True})
        print(f"[INFO] User {user_id} marked with a deletion request.")
        return {"message": "User deletion request processed successfully."}

    except Exception as e:
        print(f"[ERROR] requestUserDeletion failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while processing the deletion request."
        )



@https_fn.on_call()
def requestUserData(req: https_fn.CallableRequest):
    """
    Flags the user's Firestore document as having requested access to their personal data.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - message (str): Confirmation of data request.

    📝 Notes:
        - Sets the "data_requested" field in the user's Firestore document.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] requestUserData called for user: {user_id}")

    try:
        user_doc_ref = db.collection("users").document(user_id)
        if not user_doc_ref.get().exists:
            print(f"[ERROR] User document not found for data request: {user_id}")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="User document does not exist."
            )

        user_doc_ref.update({"data_requested": True})
        print(f"[INFO] User {user_id} marked with a data request.")
        return {"message": "User data request processed successfully."}

    except Exception as e:
        print(f"[ERROR] requestUserData failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while processing the user data request."
        )



@https_fn.on_call()
def userOnboarded(req: https_fn.CallableRequest):
    """
    Marks the authenticated user as onboarded.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - message (str): Confirmation that the user has been marked as onboarded.

    📝 Notes:
        - Updates the "onboarded" field in the user's Firestore document to True.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] userOnboarded called for user: {user_id}")

    try:
        user_doc_ref = db.collection("users").document(user_id)
        if not user_doc_ref.get().exists:
            print(f"[ERROR] User document not found for onboarding: {user_id}")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="User document does not exist."
            )

        user_doc_ref.update({"onboarded": True})
        print(f"[INFO] User {user_id} marked as onboarded.")
        return {"message": "User successfully marked as onboarded."}

    except Exception as e:
        print(f"[ERROR] userOnboarded failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while processing the user onboarding."
        )



@https_fn.on_call()
def privacyReminderDismissed(req: https_fn.CallableRequest):
    """
    Marks the user as having dismissed the privacy reminder.

    🔐 Authentication:
        - Requires an authenticated user.

    📤 Output:
        - message (str): Confirmation that the user dismissed the privacy reminder.

    📝 Notes:
        - Sets the "privacy_reminder_dismissed" field in the user's Firestore document.
    """
    if req.auth is None or req.auth.uid is None:
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
            message="The function must be called by an authenticated user."
        )

    user_id = req.auth.uid
    print(f"[INFO] privacyReminderDismissed called for user: {user_id}")

    try:
        user_doc_ref = db.collection("users").document(user_id)
        if not user_doc_ref.get().exists:
            print(f"[ERROR] User document not found for privacy dismissal: {user_id}")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.NOT_FOUND,
                message="User document does not exist."
            )

        user_doc_ref.update({"privacy_reminder_dismissed": True})
        print(f"[INFO] User {user_id} marked as having dismissed the privacy reminder.")
        return {"message": "User successfully marked as having dismissed the privacy reminder."}

    except Exception as e:
        print(f"[ERROR] privacyReminderDismissed failed: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message="An unexpected error occurred while processing the privacy reminder dismissal."
        )




