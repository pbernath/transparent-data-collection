package com.thesisproject.thesis_vt25.data.repository

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.thesisproject.thesis_vt25.data.model.Contract
import com.thesisproject.thesis_vt25.data.model.User
import kotlinx.coroutines.tasks.await

class RemoteRepositoryImpl : RemoteRepository {
    val functions = FirebaseFunctions.getInstance()

    /**
     * Calls the Cloud Function 'getRandomMessage' to fetch a random message
     * from Firestore and attempts to extract the 'text' field as a String.
     *
     * This is a suspend function and must be called from a Coroutine scope.
     *
     * @return The text content of the random message.
     * @throws Exception if the Cloud Function call fails, no messages are found,
     *                   or the returned data structure is unexpected or missing the 'text' field.
     */
    override suspend fun getAvailableContracts(): ArrayList<Contract>? {
        try {
            // Call the 'getRandomMessage' Callable function.
            // Use .await() to suspend the coroutine until the Task completes.
            // Since your Python function doesn't expect input, pass null or an empty map.
            val result = functions.getHttpsCallable("getAvailableContracts")
                .call(null) // Pass null as no input is expected by the Python function
                .await() // This suspends until the function call is done and result is ready or an exception occurs
            var contractList = ArrayList<Contract>()
            val responseData = result.data
            //Log.d("My Cloud Function Response 1.1.1", responseData.toString())
            contractList = processAvailableContracts(responseData)?: return null

            //Log.d("My Cloud Function Response 1.1.2", contractList.toString())
            return contractList

        } catch (e: FirebaseFunctionsException) {
            // Handle errors specifically from the Cloud Function call.
            when (e.code) {
                FirebaseFunctionsException.Code.NOT_FOUND -> {
                    // This corresponds to the HttpsError(code=NOT_FOUND) you might raise
                    // in your Python function if no messages are found.
                    throw NoSuchElementException("No contracts available in Firestore.")
                }
                // Add more specific error handling here if needed for other codes
                // like INTERNAL, UNAVAILABLE, DEADLINE_EXCEEDED, etc.
                else -> {
                    // For all other Firebase Functions exceptions, re-throw or handle generically.
                    // The e.details might contain the message from your HttpsError if you provided one.
                    println("Firebase Function error: Code=${e.code}, Details=${e.details}, Message=${e.message}")
                    throw Exception("Cloud Function call failed: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            // Catch any other exceptions (network issues, parsing errors, etc.)
            println("Error during contracts fetch: ${e.message}")
            throw Exception("Failed to fetch contracts: ${e.message}", e)
        }
    }
    /**
     * Calls the Cloud Function 'getRandomMessage' to fetch a random message
     * from Firestore and attempts to extract the 'text' field as a String.
     *
     * This is a suspend function and must be called from a Coroutine scope.
     *
     * @return The text content of the random message.
     * @throws Exception if the Cloud Function call fails, no messages are found,
     *                   or the returned data structure is unexpected or missing the 'text' field.
     */
    override suspend fun getInteractedContracts():  ArrayList<Contract>? {
        try {
            // Call the 'getRandomMessage' Callable function.
            // Use .await() to suspend the coroutine until the Task completes.
            // Since your Python function doesn't expect input, pass null or an empty map.
            val result = functions.getHttpsCallable("getInteractedContracts")
                .call(null) // Pass null as no input is expected by the Python function
                .await() // This suspends until the function call is done and result is ready or an exception occurs

            // The 'result.data' contains the payload returned by your Python function.
            // Recall your Python function returns {"id": "...", "data": {...}}
            val responseData = result.data
            //Log.d("My Cloud Function Response 1.2.1", responseData.toString())
            var contractList = ArrayList<Contract>()
            contractList = processInteractedContracts(responseData)?: return null

            //Log.d("My Cloud Function Response 1.2.2", contractList.toString())
            return contractList

        } catch (e: FirebaseFunctionsException) {
            // Handle errors specifically from the Cloud Function call.
            when (e.code) {
                FirebaseFunctionsException.Code.NOT_FOUND -> {
                    // This corresponds to the HttpsError(code=NOT_FOUND) you might raise
                    // in your Python function if no messages are found.
                    throw NoSuchElementException("No contracts available in Firestore.")
                }
                // Add more specific error handling here if needed for other codes
                // like INTERNAL, UNAVAILABLE, DEADLINE_EXCEEDED, etc.
                else -> {
                    // For all other Firebase Functions exceptions, re-throw or handle generically.
                    // The e.details might contain the message from your HttpsError if you provided one.
                    println("Firebase Function error: Code=${e.code}, Details=${e.details}, Message=${e.message}")
                    throw Exception("Cloud Function call failed: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            // Catch any other exceptions (network issues, parsing errors, etc.)
            println("Error during contracts fetch: ${e.message}")
            throw Exception("Failed to fetch contracts: ${e.message}", e)
        }
    }

    override suspend fun handleContractInteraction(contractId: String, action: String): String {
        val functions = FirebaseFunctions.getInstance()
        val data = hashMapOf(
            "contractId" to contractId,
            "action" to action
        )

        return try {
            val result = functions
                .getHttpsCallable("handleContractInteraction")
                .call(data)
                .await()

            val message = (result.data as Map<*, *>)["message"] as? String
            Log.d("FirebaseFunction", "Success: $message")
            message ?: "No message returned."
        } catch (e: Exception) {
            Log.e("FirebaseFunction", "Error: ${e.message}", e)
            throw e
        }
    }

    override suspend fun getUserInfo(): User? {

        try {
            Log.d("User", "Updating user info.")
            // Call the 'getRandomMessage' Callable function.
            // Use .await() to suspend the coroutine until the Task completes.
            // Since your Python function doesn't expect input, pass null or an empty map.
            val result = functions.getHttpsCallable("getUserInfo")
                .call(null) // Pass null as no input is expected by the Python function
                .await() // This suspends until the function call is done and result is ready or an exception occurs

            // The 'result.data' contains the payload returned by your Python function.
            // Recall your Python function returns {"id": "...", "data": {...}}
            val responseData = result.data
            val parsedUser = processUserInfo(responseData)
            //Log.d("Cloud Function Response 1.3.1", responseData.toString())
            return parsedUser

        } catch (e: FirebaseFunctionsException) {
            // Handle errors specifically from the Cloud Function call.
            when (e.code) {
                FirebaseFunctionsException.Code.NOT_FOUND -> {
                    // This corresponds to the HttpsError(code=NOT_FOUND) you might raise
                    // in your Python function if no messages are found.
                    throw NoSuchElementException("No user found in Firestore.")
                }
                // Add more specific error handling here if needed for other codes
                // like INTERNAL, UNAVAILABLE, DEADLINE_EXCEEDED, etc.
                else -> {
                    // For all other Firebase Functions exceptions, re-throw or handle generically.
                    // The e.details might contain the message from your HttpsError if you provided one.
                    println("Firebase Function error: Code=${e.code}, Details=${e.details}, Message=${e.message}")
                    throw Exception("Cloud Function call failed: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            // Catch any other exceptions (network issues, parsing errors, etc.)
            println("Error during contracts fetch: ${e.message}")
            throw Exception("Failed to fetch contracts: ${e.message}", e)
        }
    }

    override suspend fun setUserOnboarded() {
        try {
            val result = functions
                .getHttpsCallable("userOnboarded")
                .call(null)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseFunction", "Error: ${e.message}", e)
            throw e
        }
    }
    override suspend fun setReminderDismissed() {
        try {
            val result = functions
                .getHttpsCallable("privacyReminderDismissed")
                .call(null)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseFunction", "Error: ${e.message}", e)
            throw e
        }
    }
    override suspend fun setDataRequested() {
        try {
            val result = functions
                .getHttpsCallable("requestUserData")
                .call(null)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseFunction", "Error: ${e.message}", e)
            throw e
        }
    }
    override suspend fun setDeletionRequested() {
        try {
            val result = functions
                .getHttpsCallable("requestUserDeletion")
                .call(null)
                .await()
        } catch (e: Exception) {
            Log.e("FirebaseFunction", "Error: ${e.message}", e)
            throw e
        }
    }
    fun processUserInfo(responseData: Any?): User {
        val data = responseData as Map<*, *>
        return User(
            //creation_time = data["creation_time"] as? String,
            credits = (data["credits"] as? Number)?.toInt() ?: 0,
            id = (data["user_id"] as? String).toString(),
            //photo_url = data["photo_url"] as? String,
            //deletion_requested = data["deletion_requested"] as? Boolean ?: false,
            displayName = (data["display_name"] as? String)?: "No display name",
            onboarded = (data["onboarded"] as? Boolean) == true,
            email = (data["email"] as? String).toString(),
            privacyReminderDismissed = (data["privacy_reminder_dismissed"] as? Boolean) == true,

        )
    }
    fun processAvailableContracts(responseData: Any?) : ArrayList<Contract>? {
        val contractList = ArrayList<Contract>()
        val map = responseData as? Map<*, *> ?: return null
        val contractsRawList = map["contracts"] as? List<*> ?: return null
        for (contract in contractsRawList) {
            val contractMap = contract as? Map<*, *> ?: continue
            val id = contractMap["contract_id"] as? String ?: continue
            val title = contractMap["title"] as? String ?: continue
            val overview = contractMap["overview"] as? String ?: continue
            val reward = (contractMap["reward"] as? Number)?.toInt() ?: continue
            val description = contractMap["description"] as? String ?: continue
            val permissions =
                (contractMap["permissions"] as? List<*>)?.filterIsInstance<String>()?: continue
            val start = contractMap["start"] as? String?: continue
            val end = contractMap["end"] as? String?: continue
            val organization = contractMap["organization"] as String

            val contract = Contract(
                id = id,
                title = title,
                overview = overview,
                reward = reward,
                description = description,
                permissions = permissions,
                start = start.dropLast(7),
                end = end.dropLast(7),
                organization = organization
            )
            contractList.add(contract)
        }
        return contractList
    }

    fun processInteractedContracts(responseData: Any?) : ArrayList<Contract>? {
        val contractList = ArrayList<Contract>()
        val map = responseData as? Map<*, *> ?: return null
        val contractsRawList = map["contracts"] as? List<*> ?: return null
        for (contract in contractsRawList) {
            val contractMap = contract as? Map<*, *> ?: continue

            val id = contractMap["contract_id"] as? String ?: continue
            val interactionId= contractMap["interacted_id"] as? String
            val title = contractMap["title"] as? String ?: continue
            val overview = contractMap["overview"] as? String ?: continue
            val reward = (contractMap["reward"] as? Number)?.toInt() ?: continue
            val description = contractMap["description"] as? String
            val permissions = (contractMap["permissions"] as? List<*>)?.filterIsInstance<String>()?: continue
            val start = contractMap["start"] as? String?: continue
            val end = contractMap["end"] as? String?: continue
            val ignored = contractMap["ignored"] as? Boolean
            val accepted = contractMap["accepted"] as? Boolean
            val interrupted = contractMap["interrupted"] as? Boolean
            val created = contractMap["created"] as? String?: continue
            val finished = contractMap["finished"] as? String
            val organization = contractMap["organization"] as String

            val contract = Contract(
                id = id,
                interactionId = interactionId,
                title = title,
                overview = overview,
                reward = reward,
                description = description,
                permissions = permissions,
                start = start.dropLast(7),
                end = end.dropLast(7),
                ignored = ignored,
                accepted = accepted,
                interrupted = interrupted,
                created = created,
                finished = finished,
                organization = organization,
                status = if (interrupted == true) "Interrupted"
                else contractMap["status"] as? String
            )
            contractList.add(contract)
        }
        return contractList
    }


}