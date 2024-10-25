package com.aaronseaton.accounts.data.repository

import com.aaronseaton.accounts.domain.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
) : AuthRepository {

    override var currentUser: AuthRepository.AuthUser? = auth.currentUser?.toAuthUser()
    override var listener: AuthRepository.AuthStateListener? = null
    override suspend fun signOut() {
        auth.signOut()
        currentUser = auth.currentUser?.toAuthUser()
        onAuthStateChange(currentUser)
    }

    private suspend fun onAuthStateChange(currentUser: AuthRepository.AuthUser?){
        listener?.onAuthStateChange(currentUser)
    }

    override suspend fun signIn(credential: Any?) {
        auth.signInWithCredential(credential as AuthCredential).await()
        currentUser = auth.currentUser?.toAuthUser()
        onAuthStateChange(currentUser)
    }

    private fun FirebaseUser.toAuthUser() = this.let {
        AuthRepository.AuthUser(
            it.uid,
            it.displayName?.substringBefore(" "),
            it.displayName?.substringAfterLast(" "),
            it.email,
            it.photoUrl.toString(),
            it.phoneNumber.toString()
        )
    }
}
