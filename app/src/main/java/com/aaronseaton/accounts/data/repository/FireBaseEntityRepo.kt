package com.aaronseaton.accounts.data.repository

import android.util.Log
import com.aaronseaton.accounts.domain.repository.GenericQuery
import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FireBaseEntityRepo<T: FirebaseEntity>(
    private val firebase: FirebaseFirestore,
    private var path: String,
    private val clazz: Class<T>
): EntityRepo<T> {

    override suspend fun list(genericQuery: GenericQuery): List<T> =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "${clazz.simpleName} List: Started")
            val query = genericQuery.toFirebaseQuery()
            val listOfObjects = query
                .toList(clazz)
                .also { Log.d(TAG, "${clazz.simpleName} Live: Successful")  }
            listOfObjects
        }

    override fun liveList(genericQuery: GenericQuery): Flow<List<T>> = flow {
        Log.d(TAG, "${clazz.simpleName} Flow: Started")
        val query = genericQuery.toFirebaseQuery()
        try {
            val listOfObjects = query.toList(clazz)
            emit(listOfObjects)
        } catch (exception: Exception) {
            println("Exception in ${clazz.simpleName}: ${exception.localizedMessage}")
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun get(id: String): T = withContext(Dispatchers.IO) {
        Log.d(TAG, "${clazz.simpleName} Getting: ID - $id")
        firebase
            .collection(path)
            .document(id)
            .get()
            .await()
            .toObject(clazz)!!
    }

    override suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        Log.d(TAG, "${clazz.simpleName} Deleting: ID - $id")
        firebase
            .collection(path)
            .document(id)
            .delete()
        Unit
    }

    override suspend fun update(id: String?, update: T) = withContext(Dispatchers.IO) {
        Log.d(TAG, "${clazz.simpleName} Updating: ID - $id")
        val documentRef = if(id.isNullOrBlank()) {
            firebase.collection(path).document()
        } else {
            firebase.collection(path).document(id)
        }
        try {
            documentRef
                .set(update)
            Unit
        } catch (e: Error) {
            Log.d(TAG, e.stackTraceToString())
            Unit
        }
    }

    override suspend fun add(entity: T) = withContext(Dispatchers.IO) {
        Log.d(TAG, "${clazz.simpleName} Adding: $entity")
        firebase
            .collection(path)
            .add(entity)
        Unit
    }

    private suspend  fun <T> Query.toList (clazz: Class<T>): List<T> =
        this
            .get()
            .await()
            .toObjects(clazz)

    private fun GenericQuery.toFirebaseQuery(): Query =
        when (this) {
            is GenericQuery.WhereQuery -> firebase.collection(path)
                .whereEqualTo(this.field, this.value)

            is GenericQuery.OrderQuery -> firebase.collection(path).orderBy(
                this.field, when (this.direction) {
                    GenericQuery.DirectionOrder.ASC -> Query.Direction.ASCENDING
                    GenericQuery.DirectionOrder.DESC -> Query.Direction.DESCENDING
                }
            )

            is GenericQuery.LimitQuery -> firebase.collection(path)
                .limit(this.number.toLong())

            is GenericQuery.NoQuery -> firebase.collection(path)
        }

    companion object {
        const val TAG = "EntityRepo"
    }
}

