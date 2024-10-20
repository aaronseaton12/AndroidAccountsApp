package com.aaronseaton.accounts.domain.repository
import android.util.Log
import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

interface EntityRepo<T> {
    suspend fun list(): List<T>
    fun liveList(): Flow<List<T>>
    suspend fun get(id:String): T
    suspend fun add (entity: T)
    suspend fun update(id: String, update: T)
    suspend fun delete(id: String)
}

class FireBaseEntityRepo<T: FirebaseEntity>(
    private val firebase: FirebaseFirestore,
    private var path: String,
    private val clazz: Class<T>
): EntityRepo<T>{
    override suspend fun list(): List<T> {
        return firebase
            .collection(path)
            .get()
            .await()
            .toObjects(clazz)
    }

    override fun liveList(): Flow<List<T>> = flow {
        val ref = firebase.collection(path)
        Log.d(TAG, "${clazz.simpleName} Flow: Started")
        try {
            val response = ref.get().await().toObjects(clazz).also {
                Log.d(TAG, "${clazz.simpleName}: Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception in ${clazz.simpleName}: ${exception.localizedMessage}")
        }
    }

    override suspend fun get(id: String): T {
        return firebase
            .collection(path)
            .document(id)
            .get()
            .await()
            .toObject(clazz)!!
    }

    override suspend fun delete(id: String) {
        firebase
            .collection(path)
            .document(id)
            .delete()
    }

    override suspend fun update(id: String, update: T) {
        Log.d(TAG, "Updating: Path=$path, Document=$id")
        try {
            firebase
                .collection(path)
                .document(id)
                .set(update)
        }catch (e: Error){
            Log.d(TAG, e.stackTraceToString())
        }

    }

    override suspend fun add(entity: T) {
        firebase
            .collection(path)
            .add(entity)
    }
}
const val TAG = "EntityRepo"



