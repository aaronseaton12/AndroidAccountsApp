package com.aaronseaton.accounts.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.repository.AuthRepository
import com.aaronseaton.accounts.domain.model.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class LoginViewModel @Inject constructor(
    private val auth: AuthRepository
) : ViewModel() {
    private val loginState =
        if (auth.currentUser != null) LoadingState.LOGGED_IN else LoadingState.LOGGED_OUT

    val loadingState = MutableStateFlow(loginState).also { Log.d(TAG, "Check $it") }

    init {
        auth.addAuthStateListener {
            if (it != null) {
                Log.d(TAG, "Logged In ${auth.currentUser}")
                loadingState.emit(LoadingState.LOGGED_IN)
            } else {
                Log.d(TAG, "Logged Out")
                loadingState.emit(LoadingState.LOGGED_OUT)
            }
        }
    }

    //relating to login
    fun signWithCredential(credential: Any) = viewModelScope.launch {
        try {
            loadingState.emit(LoadingState.LOADING)
            Log.d(TAG, "Loading $credential")
            auth.signIn(credential)
            Log.d(TAG, "Finished Loading")
        } catch (e: Exception) {
            loadingState.emit(LoadingState.error(e.localizedMessage))
        }
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
        }
    }

    companion object {
        const val TAG = "LoginViewModel"
    }
}