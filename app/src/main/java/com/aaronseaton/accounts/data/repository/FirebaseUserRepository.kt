package com.aaronseaton.accounts.data.repository

import android.util.Log
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.AuthRepository
import com.aaronseaton.accounts.domain.repository.UserRepository
import com.aaronseaton.accounts.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepository @Inject constructor (
    private val authRepository: AuthRepository,
    private val db: FirebaseFirestore,
) : UserRepository {
    private val ref = db.collection(USERS)
    override val accountUser = callbackFlow {
        val registration = authRepository.currentUser?.uid?.let { userID ->
            ref.document(userID).addSnapshotListener{ snapshot, e ->
                if(e != null){
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot == null) {
                    Log.w(TAG, "Snapshot is null")
                    return@addSnapshotListener
                }
                val currentUser = snapshot.toObject(User::class.java)!!.also {
                    val message = "${it.fullName()}\nBusiness: ${it.selectedBusiness}"
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
    override suspend fun update (userID: String, user: User): Unit = withContext(Dispatchers.IO){
        ref.document(userID).set(user).await()
    }
    override suspend fun get (userID: String): User = withContext(Dispatchers.IO){
       ref
            .document(userID)
            .get()
            .await()
            .toObject(User::class.java)!!
    }
    override fun liveList() = flow {
        Log.d(TAG, "${User::class.java.simpleName} Flow: Started")
        try {
            val response = ref.get().await().toObjects(User::class.java).also {
                Log.d(TAG, "${User::class.java.simpleName}: Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception in ${User::class.java.simpleName}: ${exception.localizedMessage}")
        }
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
