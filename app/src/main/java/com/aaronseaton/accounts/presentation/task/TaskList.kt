package com.aaronseaton.accounts.presentation.task

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.presentation.components.AccountDivider
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.util.Routes

private const val TAG = "TaskList"

@Composable
fun ListOfTasks(
    navigateTo: (String) -> Unit,
    viewModel: TasksViewModel = hiltViewModel()
) {
    Log.d(TAG, "List of Tasks")
    val state by viewModel.state.collectAsState(TaskListScreenState())
    ListOfTasksImpl(
        state,
        navigateTo,
        viewModel::updateTask,
        viewModel::changeSorting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfTasksImpl(
    state: TaskListScreenState,
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit,
    changeSorting: (Sorting.TaskSorting) -> Unit
) {
    Log.d(TAG, "List of Tasks Impl")
    val icon = Icons.AutoMirrored.Filled.ArrowBack
    val onPressed = { navigateTo(Routes.HOME) }
    val description = "Home"
    val title = "Task List"
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        IconButton(onClick = onPressed) {
                            Icon(icon, description)
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort By Date") },
                                onClick = { changeSorting(Sorting.TaskSorting.BY_DUE_DATE) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Assigned To") },
                                onClick = { changeSorting(Sorting.TaskSorting.BY_ASSIGNED_TO) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Priority") },
                                onClick = { changeSorting(Sorting.TaskSorting.BY_PRIORITY) }
                            )
                        }

                    }
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.TASK_LIST) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Task") },
                icon = { Icon(Icons.Default.Add, "Add Task") },
                onClick = { navigateTo(Routes.ADD_TASK) }
            )
        }
    ) { padding ->
        when (state.loading) {
            true -> LoadingScreen()
            false -> ListOfTasksContent(
                state.tasks,
                state.sorting,
                state.users,
                navigateTo,
                updateTask,
                Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun ListOfTasksContent(
    tasks: List<Task>,
    sorting: Sorting.TaskSorting,
    users: List<User>,
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit,
    modifier: Modifier
) {
    Log.d(TAG, "List of Tasks Content")
    val sortedTasks = when (sorting) {
        Sorting.TaskSorting.BY_DUE_DATE -> tasks.sortedBy { it.dueDate }
        Sorting.TaskSorting.BY_ASSIGNED_TO -> tasks.sortedBy { task ->
            task.assignedTo.let { id ->
                users.single { it.documentID == id }
            }.firstName
        }
        Sorting.TaskSorting.BY_PRIORITY -> tasks //tasks.sortedBy { it.priority }.reversed()
    }

    val completedTasks = sortedTasks.filter { it.done }
    val incompleteTasks = sortedTasks.filter { it.done.not() }
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { Text(text = "Incomplete", style = labelStyle) }
        item { AccountDivider() }
        items(incompleteTasks) { task ->
            TaskCard(
                task = task,
                user = users.find { it.documentID == task.assignedTo } ?: User(),
                navigateTo = navigateTo,
                updateTask = updateTask
            )
        }
        if (completedTasks.isNotEmpty()) {
            item { Text(text = "Completed", style = labelStyle) }
            items(completedTasks) { task ->
                TaskCard(
                    task = task,
                    user = users.find { it.documentID == task.assignedTo } ?: User(),
                    navigateTo = navigateTo,
                    updateTask = updateTask
                )
            }
        }
    }
}

