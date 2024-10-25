package com.aaronseaton.accounts.presentation.user

import androidx.lifecycle.ViewModel
import com.aaronseaton.accounts.data.repository.FirebaseRepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class TestUserVM @Inject constructor(
    private val repoGroup: FirebaseRepoGroup,

    ) : ViewModel() {
    val state = repoGroup.repos.map { repos->
        val accountUser = repos.accountUser
        val selectedBusiness = repos.business.get(accountUser.selectedBusiness)
            UserState(
                user = accountUser,
                selectedBusiness = selectedBusiness,
                usersBusinesses = repos.business.list().filter { it.members.contains(accountUser.documentID) },
                loading = false
            )
        }
    }
