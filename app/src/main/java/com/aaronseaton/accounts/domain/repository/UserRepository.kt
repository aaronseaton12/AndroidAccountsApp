package com.aaronseaton.accounts.domain.repository

import android.util.Log
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor (
    private val authRepository: AuthRepository,
    private val db: FirebaseFirestore,
) {
    val accountUser = callbackFlow {
        val registration = authRepository.currentUser?.uid?.let { userID ->
            db.collection(USERS).document(userID).addSnapshotListener{ snapshot, e ->
                if(e != null){
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                val currentUser = snapshot?.toObject(User::class.java).also {
                    val message = if(it == null) "NULL" else "${it.fullName()}\nBusiness: ${it.selectedBusiness}"
                    Log.d(TAG, "Current User: $message")
                }
                trySendBlocking(currentUser)
            }
        }
        awaitClose {registration?.remove()}
    }
    companion object {
        const val TAG = "UserRepository"
    }
}

/**
 * emit(null)
 *         try {
 *             val userResponse = authRepository
 *                 .currentUser
 *                 ?.uid
 *                 ?.let {
 *                     db.collection(USERS)
 *                         .document(it)
 *                         .get()
 *                         .await()
 *                         .toObject(User::class.java) }
 *             emit(userResponse)
 *         }catch (exception: Exception){
 *             println("Error occurred in getting current user")
 *         }
 */
