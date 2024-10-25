package com.aaronseaton.accounts.data.repository

import com.aaronseaton.accounts.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {
    override var currentUser: AuthRepository.AuthUser? = AuthRepository.AuthUser()
    override var listener: AuthRepository.AuthStateListener? = null

    override suspend fun signOut() {
        currentUser = null
        listener?.onAuthStateChange(currentUser)
    }

    override suspend fun signIn(credential: Any?) {
        currentUser = AuthRepository.AuthUser(firstName = "Aaron")
        listener?.onAuthStateChange(currentUser)
    }
}