package com.aaronseaton.accounts.domain.repository

import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.aaronseaton.accounts.domain.model.Response
import com.aaronseaton.accounts.domain.model.Response.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface EntityRepository<T : FirebaseEntity> {
    fun entityFlow(): Flow<Response<List<T>>>
    fun getEntity(entityID: String): Flow<Response<T>>
    fun insertEntity(entity: T): Flow<Response<Void?>>
    fun updateEntity(entity: T): Flow<Response<Void?>>
    fun deleteEntity(entityID: String): Flow<Response<Void?>>
}

class EntityRepositoryImpl<T : FirebaseEntity> @Inject constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val path: String,
    private val clazz: Class<T>
) : EntityRepository<T> {
    val ref = db.collection(path)
    override fun entityFlow() = flow {
        try {
            emit(Loading)
            val data = ref.get().await().toObjects(clazz)
            emit(Success(data))
        } catch (exception: Exception) {
            emit(Failure(exception))
        }
    }

    override fun getEntity(entityID: String) = flow {
        try {
            emit(Loading)
            val entity = ref
                .document(entityID)
                .get()
                .await()
                .toObject(clazz)
            emit(Success(entity))
        } catch (exception: Exception) {
            emit(Failure(exception))
        }
    }

    override fun deleteEntity(entityID: String) = flow {
        try {
            emit(Loading)
            val entity = ref
                .document(entityID)
                .delete()
                .await()
            emit(Success(entity))
        } catch (exception: Exception) {
            emit(Failure(exception))
        }
    }

    override fun updateEntity(entity: T) = flow {
        try {
            emit(Loading)
            val entityUpdated = ref
                .document(entity.documentID)
                .set(entity)
                .await()
            emit(Success(entityUpdated))
        } catch (exception: Exception) {
            emit(Failure(exception))
        }
    }

    override fun insertEntity(entity: T) = flow {
        try {
            emit(Loading)
            val id = ref.document().id
            val entityAdded = ref
                .document(id)
                .set(entity)
                .await()
            emit(Success(entityAdded))
        } catch (exception: Exception) {
            emit(Failure(exception))
        }
    }
}


