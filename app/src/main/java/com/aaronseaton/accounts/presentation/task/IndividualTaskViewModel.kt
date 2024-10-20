package com.aaronseaton.accounts.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Task
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

@HiltViewModel
class IndividualTaskViewModel @Inject constructor(
    private val accountUser: User,
    private val businessRepo: EntityRepo<Business>,
    private val taskRepo: EntityRepo<Task>,
    private val userRepo: EntityRepo<User>
) : ViewModel() {

    private val _taskIndividual: MutableStateFlow<IndividualTaskState> =
        MutableStateFlow(IndividualTaskState(loading = true))
    val taskIndividualState: StateFlow<IndividualTaskState> = _taskIndividual.asStateFlow()

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepo.delete(task.documentID)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepo.update(task.documentID, task)
        }
    }

    fun updateIndividualTaskState(taskID: String) {
        viewModelScope.launch {
            val task = taskRepo.get(taskID)
            val accountUser = accountUser
            val business = businessRepo.get(accountUser.selectedBusiness)
            userRepo.liveList().map {
                it.filter { user -> business.members.contains(user.documentID) }
            }.collect { users ->
                _taskIndividual.update {
                    it.copy(
                        task = task,
                        accountUser = accountUser,
                        users = users,
                        loading = false
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "ListOfTasksViewModel"
    }
}

data class IndividualTaskState(
    val task: Task = Task(),
    val accountUser: User = User(),
    val users: List<User> = listOf(User()),
    val loading: Boolean = false
)