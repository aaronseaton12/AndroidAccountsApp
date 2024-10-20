package com.aaronseaton.accounts.presentation.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "User ViewModel"
data class UserState(
    val user: User = User(),
    val selectedBusiness: Business = Business(),
    val usersBusinesses: List<Business> = listOf(Business()),
    val loading: Boolean = true
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val accountUser: User,
    private val userRepo: EntityRepo<User>,
    private val businessRepo: EntityRepo<Business>,
) : ViewModel() {
    private val _state = MutableStateFlow(UserState(loading = true))
    val state: StateFlow<UserState> = _state.asStateFlow()


    init {
        viewModelScope.launch {
            val user = accountUser
            val selectedBusiness: Business = businessRepo.get(accountUser.selectedBusiness)
                .also { println("UserVM Init SelectBusiness: ${it.name}") }
            _state.update {
                it.copy(
                    user = user,
                    selectedBusiness = selectedBusiness,
                    loading = false
                )
            }
            businessRepo.liveList()
                .map { it.filter { business -> business.members.contains(user.documentID) } }
                .collect { userBusinesses ->
                    _state.update {
                        it.copy(
                            usersBusinesses = userBusinesses
                        )
                    }
                }
            _state.value.selectedBusiness.also { println("From ViewModel ${it.name}") }
        }
    }

    fun onChangeSelectedBusiness(business: Business) {
        viewModelScope.launch {
            Log.d(TAG, "Selected business: ${business.documentID}")
            val updatedUser = accountUser.copy(selectedBusiness = business.documentID)
            Log.d(TAG, "Users business: ${updatedUser.selectedBusiness}")
            updateUser(updatedUser)
            _state.update {
                it.copy(
                    selectedBusiness = business,
                )
            }.also { Log.d(TAG, "User's business: ${_state.value.user.selectedBusiness}") }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepo.update(user.documentID, user)
        }
    }
}
