package com.aaronseaton.accounts.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.TaskSorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val accountUser: User,
    private val businessRepo: EntityRepo<Business>,
    private val taskRepo: EntityRepo<Task>,
    private val userRepo: EntityRepo<User>
) : ViewModel() {
    val TAG = "ListOfTasksViewModel"
    private val _taskList: MutableStateFlow<TaskListModel> =
        MutableStateFlow(TaskListModel(loading = true))
    val taskList: StateFlow<TaskListModel> = _taskList.asStateFlow()

    init {
        viewModelScope.launch {
            refreshList()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepo.update(task.documentID, task)
        }
        refreshList()
    }

    fun changeSorting(sorting: TaskSorting) {
        viewModelScope.launch {
            _taskList.update {
                it.copy(sorting = sorting)
            }
        }
    }

    private fun refreshList() = viewModelScope.launch {
        val accountUser = accountUser
        val business = async { businessRepo.get(accountUser.selectedBusiness) }
        combine(
            userRepo.liveList(), taskRepo.liveList()
        ) { users, tasks ->
            val businessUsers = users.filter { business.await().members.contains(it.documentID) }
            TaskListModel(
                accountUser = accountUser,
                users = businessUsers,
                loading = false,
                tasks = tasks
            )
        }.collect { taskListState ->
            _taskList.update {
                it.copy(
                    accountUser = taskListState.accountUser,
                    users = taskListState.users,
                    tasks = taskListState.tasks,
                    loading = taskListState.loading

                )
            }
        }
    }
}

data class TaskListModel(
    val tasks: List<Task> = listOf(Task()),
    val users: List<User> = listOf(User()),
    val accountUser: User = User(),
    val sorting: TaskSorting = TaskSorting.BY_DUE_DATE,
    val loading: Boolean = false
)
