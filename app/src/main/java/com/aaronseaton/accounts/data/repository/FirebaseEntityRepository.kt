package com.aaronseaton.accounts.data.repository

import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.aaronseaton.accounts.domain.model.Response
import com.aaronseaton.accounts.domain.repository.EntityRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEntityRepository<T : FirebaseEntity> @Inject constructor(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val path: String,
    private val clazz: Class<T>
) : EntityRepository<T> {
    private val ref = db.collection(path)
    override fun entityFlow() = flow {
        try {
            emit(Response.Loading)
            val data = ref.get().await().toObjects(clazz)
            emit(Response.Success(data))
        } catch (exception: Exception) {
            emit(Response.Failure(exception))
        }
    }

    override fun getEntity(entityID: String) = flow {
        try {
            emit(Response.Loading)
            val entity = ref
                .document(entityID)
                .get()
                .await()
                .toObject(clazz)
            emit(Response.Success(entity))
        } catch (exception: Exception) {
            emit(Response.Failure(exception))
        }
    }

    override fun deleteEntity(entityID: String) = flow {
        try {
            emit(Response.Loading)
            val entity = ref
                .document(entityID)
                .delete()
                .await()
            emit(Response.Success(entity))
        } catch (exception: Exception) {
            emit(Response.Failure(exception))
        }
    }

    override fun updateEntity(entity: T) = flow {
        try {
            emit(Response.Loading)
            val entityUpdated = ref
                .document(entity.documentID)
                .set(entity)
                .await()
            emit(Response.Success(entityUpdated))
        } catch (exception: Exception) {
            emit(Response.Failure(exception))
        }
    }

    override fun insertEntity(entity: T) = flow {
        try {
            emit(Response.Loading)
            val id = ref.document().id
            val entityAdded = ref
                .document(id)
                .set(entity)
                .await()
            emit(Response.Success(entityAdded))
        } catch (exception: Exception) {
            emit(Response.Failure(exception))
        }
    }
}