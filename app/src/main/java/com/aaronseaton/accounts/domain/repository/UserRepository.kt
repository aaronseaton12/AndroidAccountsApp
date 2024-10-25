package com.aaronseaton.accounts.domain.repository

import com.aaronseaton.accounts.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val accountUser: Flow<User>
    suspend fun update (userID: String, user: User)
    suspend fun get (userID: String): User
    fun liveList(): Flow<List<User>>
}