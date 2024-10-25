package com.aaronseaton.accounts.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repoGroup: RepoGroup
) : ViewModel() {

    private var taskRepo: EntityRepo<Task>? = null
    private var taskID: String? = null
    fun setTaskId(taskID: String?) {this.taskID = taskID}

    private val sorting = MutableStateFlow(Sorting.TaskSorting.BY_DUE_DATE)
    val state = repoGroup.repos.map{ repos ->
        coroutineScope {
            taskRepo = repos.task
            val accountUser = async { repos.accountUser }
            val business = async { repos.business.get(repos.accountUser.selectedBusiness) }
            val tasks = async { repos.task.list() }
            val users = async { repos.user.list().filter { user -> business.await().members.contains(user.documentID) } }

            TaskListScreenState(
                tasks = tasks.await(),
                users = users.await(),
                accountUser = accountUser.await(),
                loading = false
            )
        }
    }.combine(sorting){taskList, sorting ->
        taskList.copy(sorting = sorting)
    }
    val individualState = repoGroup.repos.map { repos ->
        taskRepo = repos.task
        coroutineScope {
            val task = async { if(taskID==null)Task() else repos.task.get(taskID!!) }
            val matter = async {
                if(task.await().matter.isNullOrBlank()) Matter()
                else repos.matter.get( task.await().matter!! )
            }
            TaskIndividualScreenState(
                task.await(),
                matter.await(),
                repos.accountUser,
                repos.user.list(),
                loading = false
            )
        }

    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            taskRepo?.add(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskRepo?.delete(task.documentID)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskRepo?.update(task.documentID, task)
        }
    }


    fun changeSorting(newSorting: Sorting.TaskSorting) {
        sorting.update { newSorting }
    }
    companion object{
        const val TAG = "ListOfTasksViewModel"
    }
}

data class TaskListScreenState(
    val tasks: List<Task> = listOf(Task()),
    val users: List<User> = listOf(User()),
    val accountUser: User = User(),
    val sorting: Sorting.TaskSorting = Sorting.TaskSorting.BY_DUE_DATE,
    val loading: Boolean = true
)
data class TaskIndividualScreenState(
    val task: Task = Task(),
    val matter: Matter = Matter(),
    val accountUser: User = User(),
    val users: List<User> = listOf(User()),
    val loading: Boolean = true
)