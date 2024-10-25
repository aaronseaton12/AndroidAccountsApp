package com.aaronseaton.accounts.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserState(
    val user: User = User(),
    val selectedBusiness: Business = Business(),
    val usersBusinesses: List<Business> = listOf(Business()),
    val loading: Boolean = true
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repoGroup: RepoGroup
) : ViewModel() {
    private var accountUser = User()
    private var userRepo: EntityRepo<User>? = null
    val state = repoGroup.repos.map { repos ->
        userRepo = repos.user
        accountUser = repos.accountUser
        val selectedBusiness = repos.business.get(accountUser.selectedBusiness)
        UserState(
            user = accountUser,
            selectedBusiness = selectedBusiness,
            usersBusinesses = repos.business.list().filter{ it.members.contains(accountUser.documentID) },
            loading = false
        )
    }

    fun onChangeSelectedBusiness(business: Business) {
        viewModelScope.launch {
            val updatedUser = accountUser.copy(selectedBusiness = business.documentID)
            updateUser(updatedUser)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepo?.update(user.documentID, user)
        }
    }
    companion object {
        private const val TAG = "User ViewModel"
    }
}
