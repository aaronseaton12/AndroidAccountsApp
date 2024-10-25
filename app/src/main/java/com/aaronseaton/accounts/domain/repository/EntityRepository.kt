package com.aaronseaton.accounts.domain.repository

import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.aaronseaton.accounts.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface EntityRepository<T : FirebaseEntity> {
    fun entityFlow(): Flow<Response<List<T>>>
    fun getEntity(entityID: String): Flow<Response<T>>
    fun insertEntity(entity: T): Flow<Response<Void?>>
    fun updateEntity(entity: T): Flow<Response<Void?>>
    fun deleteEntity(entityID: String): Flow<Response<Void?>>
}


