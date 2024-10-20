package com.aaronseaton.accounts.presentation.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.LoadingState
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.home.Home

private const val TAG = "LoginOrHomeScreen"

@Composable
fun LoginOrHomeScreen(
    navigateTo: (String) -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    Log.d(TAG, "Login Home Screen()")
    val loadingState by viewModel.loadingState.collectAsState()
    when (loadingState.status) {
        LoadingState.Status.SUCCESS -> {
            Home(navigateTo, viewModel::signOut)
            Log.d(TAG, "Log In Successful")
        }
        LoadingState.Status.LOADING -> {
            LoadingScreen()
        }
        LoadingState.Status.FAILED -> {
            println(loadingState.msg ?: "Error")
            LoginScreen()
        }
        LoadingState.Status.LOGGED_IN -> {
            Home(navigateTo, viewModel::signOut)
            Log.d(TAG, "Already Logged In")
        }
        LoadingState.Status.LOGGED_OUT -> {
            Log.d(TAG, "Logged Out :(")
            LoginScreen()
        }
        else -> {
            LoginScreen()
        }
    }
}