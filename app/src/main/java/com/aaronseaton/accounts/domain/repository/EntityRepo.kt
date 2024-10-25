package com.aaronseaton.accounts.domain.repository
import kotlinx.coroutines.flow.Flow

interface EntityRepo<T> {
    suspend fun list(genericQuery: GenericQuery = GenericQuery.NoQuery): List<T>
    fun liveList(genericQuery: GenericQuery = GenericQuery.NoQuery): Flow<List<T>>
    suspend fun get(id:String): T
    suspend fun add (entity: T)
    suspend fun update(id: String?, update: T)
    suspend fun delete(id: String)
}
