package com.codermonkeys.trelloclone.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.codermonkeys.trelloclone.activities.*
import com.codermonkeys.trelloclone.models.Board
import com.codermonkeys.trelloclone.models.User
import com.codermonkeys.trelloclone.utils.Constants.ASSIGNED_TO
import com.codermonkeys.trelloclone.utils.Constants.BOARDS
import com.codermonkeys.trelloclone.utils.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.sdsmdg.tastytoast.TastyToast

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "FirestoreClass"
    }

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
    }

    fun loadUserData(activity: Activity, readBoardList: Boolean = false) {
        mFireStore.collection(USERS).document(getCurrentUserId()).get()
            .addOnSuccessListener {
                val loggedInUser = it.toObject(User::class.java)
                loggedInUser?.let { user ->
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess(user)
                        }

                        is MainActivity -> {
                            activity.updateNavigationUserDetails(user, readBoardList)
                        }

                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }

                }
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Log.e(TAG, "Profile data updated succesfully ")
                TastyToast.makeText(
                    activity,
                    "Profile Updated Successfully!!",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.SUCCESS
                ).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(TAG, "${exception.message} ")
                TastyToast.makeText(
                    activity,
                    "Error occured while updating profile!!",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR
                ).show()
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(BOARDS).document().set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(TAG, "createBoard: successfyully")
                TastyToast.makeText(
                    activity,
                    "Created Board Successfully",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.SUCCESS
                ).show()
                activity.boardCreateSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
                Log.e(TAG, "createBoard: ${exception.message}")
            }
    }

    fun getBoardList(activity: MainActivity) {
        mFireStore.collection(BOARDS).whereArrayContains(ASSIGNED_TO, getCurrentUserId()).get()
            .addOnSuccessListener { document ->
                Log.e(TAG, "getBoardList: ${document.documents.toString()}")

                val boardList = ArrayList<Board>()
                for (docs in document.documents) {
                    val board = docs.toObject(Board::class.java)
                    board?.let {
                        it.documentId = docs.id
                        boardList.add(it)
                    }
                }

                activity.populateBoardListToUi(boardList)
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(TAG, "getBoardList: ${it.message}", )
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        currentUser?.let {
            currentUserID = it.uid
        }
        return currentUserID
    }
}