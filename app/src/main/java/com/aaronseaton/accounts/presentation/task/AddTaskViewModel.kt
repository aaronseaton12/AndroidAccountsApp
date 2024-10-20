package com.aaronseaton.accounts.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val accountUser: User,
    private val taskRepo: EntityRepo<Task>
) : ViewModel() {
    val TAG = "ListOfTasksViewModel"

    private val _addTask: MutableStateFlow<IndividualTaskState> =
        MutableStateFlow(IndividualTaskState(loading = true))
    val addTask: StateFlow<IndividualTaskState> = _addTask.asStateFlow()

    init {
        viewModelScope.launch {
            val accountUser = accountUser
            _addTask.update {
                it.copy(
                    accountUser = accountUser,
                    loading = false,
                )
            }
        }
    }

    fun addTask(task: Task): Unit {
        viewModelScope.launch {
            taskRepo.add(task)
        }
    }
}