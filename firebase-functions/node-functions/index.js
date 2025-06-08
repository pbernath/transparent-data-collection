// ─────────────────────────────────────────────────────────────────────────────
// Cloud Function: createNewUserDocument
// Triggered on Firebase Authentication user creation.
// Creates a new Firestore document in the 'users' collection.
//
// 🔐 Authentication:
//     - Triggered automatically by Firebase when a user is created.
//
// 📤 Output:
//     - Returns null upon successful document creation.
//
// 📝 Notes:
//     - This function runs in the context of the Firebase Authentication trigger.
//     - Initializes default user fields for onboarding and legal compliance.
//
// 📁 Firestore Collections Affected:
//     - users (Document ID = UID)
//
// 🔧 Default Values Set:
//     - credits: 0
//     - onboarded: false
//     - deletion_requested: false
//     - data_requested: false
//     - privacy_reminder_dismissed: false
// ─────────────────────────────────────────────────────────────────────────────

const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");

// Global Admin SDK & Firestore Initialization
admin.initializeApp();
const db = admin.firestore();

exports.createNewUserDocument = functions.auth.user().onCreate(async (user) => {
  const {uid, email, displayName, photoURL, metadata} = user;
  const creationTime = metadata.creationTime;

  functions.logger.log("New user created in Authentication:", uid);

  const userData = {
    email: email || null,
    display_name: displayName || null,
    photo_url: photoURL || null,
    creation_time: creationTime,
    credits: 0,
    onboarded: false,
    deletion_requested: false,
    data_requested: false,
    privacy_reminder_dismissed: false,
  };

  try {
    await db.collection("users").doc(uid).set(userData);

    functions.logger.log(
      "User document successfully created in Firestore for UID:",
      uid
    );

    return null;
  } catch (error) {
    functions.logger.error(
      "Error writing user document to Firestore for UID:",
      uid,
      error
    );
    throw error;
  }
});
