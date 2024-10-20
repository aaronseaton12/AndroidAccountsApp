package com.aaronseaton.accounts.presentation.task

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.TaskSorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.prettyDateFormatter
import com.aaronseaton.accounts.presentation.components.AccountDivider
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen

private val TAG = "TaskList"

@Composable
fun ListOfTasks(
    navigateTo: (String) -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    Log.d(TAG, "List of Tasks")
    val state by viewModel.taskList.collectAsState()
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
    state: TaskListModel,
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit,
    changeSorting: (TaskSorting) -> Unit
) {
    Log.d(TAG, "List of Tasks Impl")
    val icon = Icons.Default.ArrowBack
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
                                onClick = { changeSorting(TaskSorting.BY_DUE_DATE) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Assigned To") },
                                onClick = { changeSorting(TaskSorting.BY_ASSIGNED_TO) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Priority") },
                                onClick = { changeSorting(TaskSorting.BY_PRIORITY) }
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
    sorting: TaskSorting,
    users: List<User>,
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit,
    modifier: Modifier
) {
    Log.d(TAG, "List of Tasks Content")
    val sortedTasks = when (sorting) {
        TaskSorting.BY_DUE_DATE -> tasks.sortedBy { it.dueDate }
        TaskSorting.BY_ASSIGNED_TO -> tasks.sortedBy { task ->
            task.assignedTo.let { id ->
                users.single { it.documentID == id }
            }.firstName
        }
        TaskSorting.BY_PRIORITY -> tasks //tasks.sortedBy { it.priority }.reversed()
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

@Composable
fun TaskCard(
    task: Task,
    user: User = User(),
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateTo(Routes.INDIVIDUAL_TASK + "/" + task.documentID) },
        tonalElevation = 0.dp
    ) {
        Row {
            RadioButton(
                selected = task.done,
                onClick = { updateTask(task.copy(done = !task.done)) })
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(
                    text = task.name,
                    modifier = Modifier.fillMaxWidth(),
                    style = if (!task.done) MaterialTheme.typography.titleMedium else
                        MaterialTheme.typography.titleMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                )
                Text(
                    text = task.description,
                    modifier = Modifier.fillMaxWidth(),
                    style = if (!task.done) MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ) else
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                )
                Text(
                    text = prettyDateFormatter.format(task.dueDate),
                    modifier = Modifier.fillMaxWidth(),
                    style = if (!task.done) MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ) else
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                )
                Text(
                    text = "Assigned To: ${user.fullName}",
                    modifier = Modifier.fillMaxWidth(),
                    style = if (!task.done) MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    ) else
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                )
            }
        }
        AccountDivider()
    }
}
