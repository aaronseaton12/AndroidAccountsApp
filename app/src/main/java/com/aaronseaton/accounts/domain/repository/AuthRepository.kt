package com.aaronseaton.accounts.domain.repository

interface AuthRepository {
    var currentUser: AuthUser?
    var listener: AuthStateListener?
    fun addAuthStateListener(listener: AuthStateListener) {
        this.listener = listener
    }

    fun removeAuthStateListener() {
        this.listener = null
    }

    suspend fun signOut()
    suspend fun signIn(credential: Any? = null)

    fun interface AuthStateListener {
        suspend fun onAuthStateChange(currentUser: AuthUser?)
    }
    data class AuthUser(
        val uid: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val email: String? = null,
        val photoURL: String? = null,
        val phone: String? = null
    )
}



