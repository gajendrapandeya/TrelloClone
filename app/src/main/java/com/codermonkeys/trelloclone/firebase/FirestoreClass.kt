package com.codermonkeys.trelloclone.firebase

import com.codermonkeys.trelloclone.activities.SignUpActivity
import com.codermonkeys.trelloclone.models.User
import com.codermonkeys.trelloclone.utils.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }
    }

    private fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid!!
    }
}