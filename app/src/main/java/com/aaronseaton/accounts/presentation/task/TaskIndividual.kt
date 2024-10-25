package com.aaronseaton.accounts.presentation.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import com.aaronseaton.accounts.util.Util.Companion.timeFormatter
import com.aaronseaton.accounts.presentation.components.*
import com.aaronseaton.accounts.presentation.matter.MatterListState
import com.aaronseaton.accounts.presentation.matter.MatterViewModels
import java.util.*

@Composable
fun IndividualTask(
    taskID: String,
    viewModel: TasksViewModel = hiltViewModel(),
    navigateTo: (String) -> Unit,
    //businessID: String?
) {
    LaunchedEffect(taskID) {viewModel.setTaskId(taskID)}
    val state by viewModel.individualState.collectAsState(TaskIndividualScreenState())
    when (state.loading) {
        true -> LoadingScreen()
        false -> IndividualTaskImpl(
            state.task,
            state.users,
            state.matter,
            viewModel::updateTask,
            viewModel::deleteTask,
            navigateTo
        )
    }
}


@Composable
fun IndividualTaskImpl(
    task: Task,
    users: List<User>,
    initialMatter: Matter,
    updateTask: (Task) -> Unit,
    deleteTask: (Task) -> Unit,
    navigateTo: (String) -> Unit
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.TASK_LIST) }
    val title = "Individual Task"
    var transientTask by remember { mutableStateOf(task) }
    val onTaskChange = { newTask: Task -> transientTask = newTask }
    var showAreYouSureAboutDeleteDialog by remember { mutableStateOf(false) }
    val onAreYouSureDismissRequest = { showAreYouSureAboutDeleteDialog = false }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon, actions = {
                IconButton(onClick = {
                    updateTask(transientTask)
                    navigateTo(Routes.TASK_LIST)
                    Toast.makeText(context, "Task Saved", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Save",
                        modifier = Modifier.size(24.dp, 24.dp)
                    )
                }
                IconButton(onClick = { showAreYouSureAboutDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            })
        },
    ) {
        IndividualTaskContent(
            transientTask,
            initialMatter,
            onTaskChange,
            showAreYouSureAboutDeleteDialog,
            onAreYouSureDismissRequest,
            deleteTask,
            navigateTo,
            users,
            Modifier.padding(it)
        )
    }
}


@Composable
fun IndividualTaskContent(
    task: Task,
    initialMatter: Matter,
    onTaskDoneChange: (Task) -> Unit,
    showAreYouSureAboutDeleteDialog: Boolean,
    onAreYouSureDismissRequest: () -> Unit,
    deleteTask: (Task) -> Unit,
    navigateTo: (String) -> Unit,
    users: List<User>,
    modifier: Modifier,
    viewModel:MatterViewModels = hiltViewModel(),

) {
    val state by viewModel.state.collectAsState(MatterListState())
    fun setDate(date: Date, changeDate: (Date) -> Unit) =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            changeDate(Date(year - 1900, month, day, date.hours, date.minutes))
        }

    fun setTime(date: Date, changeTime: (Date) -> Unit) =
        TimePickerDialog.OnTimeSetListener { _, hours, minutes ->
            changeTime(Date(date.year, date.month, date.date, hours, minutes))
        }

    val context = LocalContext.current
    var isAssigneeDialogShowing: Boolean by remember { mutableStateOf(false) }
    val onAssignmentDismissRequest = { isAssigneeDialogShowing = false }
    var isCreatorDialogShowing: Boolean by remember { mutableStateOf(false) }
    val onCreatorDialogDismissRequest = { isCreatorDialogShowing = false }
    var itemPadding = PaddingValues(top = 20.dp, bottom = 3.dp)
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )
    var matter by remember { mutableStateOf(initialMatter) }
    var matterDialog by remember{mutableStateOf(false)}

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        AccountsDialog(
            showDialog = showAreYouSureAboutDeleteDialog,
            onNegativeButtonPressed = onAreYouSureDismissRequest,
            onPositiveButtonPressed = {
                deleteTask(task)
                navigateTo(Routes.TASK_LIST)
                Toast.makeText(context, "Task Deleted", Toast.LENGTH_SHORT).show()
            },
            content = {
                Text(
                    text = "Are you sure you want to delete?",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        )
        AssignedToDialog(
            isAssigneeDialogShowing = isAssigneeDialogShowing,
            onAssignmentDismissRequest = onAssignmentDismissRequest,
            assigneeID = task.assignedTo,
            onAssigneeSelected = { onTaskDoneChange(task.copy(assignedTo = it.documentID)) },
            users = users
        )
        ItemSelect(
            items = state.matterList,
            isDialogShowing = matterDialog,
            onDismissRequest = {matterDialog = false},
            filterFunction = { matters, searchText ->
                matters.filter { it.title.contains(searchText, ignoreCase = true )}},
            cardText = {it.title},
            onItemSelected = {
                onTaskDoneChange(task.copy(matter = it.documentID))
                matter = it
            }
        )

        CreatedByDialog(
            isCreatorDialogShowing = isCreatorDialogShowing,
            onCreatorDialogDismissRequest = onCreatorDialogDismissRequest,
            creatorID = task.wasCreatedBy,
            onCreatorSelected = { onTaskDoneChange(task.copy(wasCreatedBy = it.documentID)) },
            users = users
        )
        Column(Modifier.padding(itemPadding)) {
            Text(text = "Task", style = labelStyle)
            BasicTextField(
                value = task.name,
                onValueChange = { onTaskDoneChange(task.copy(name = it)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
        AccountDivider()
        Column(Modifier.padding(itemPadding)) {
            Text(text = "Info", style = labelStyle)
            BasicTextField(
                value = task.description,
                onValueChange = { onTaskDoneChange(task.copy(description = it)) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
        AccountDivider()
        Column(Modifier.padding(itemPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Due Date", style = labelStyle)
                Text(text = "Time Due", style = labelStyle)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = dateFormatter.format(task.dueDate),
                    Modifier.clickable {
                        showDatePicker(
                            context,
                            task.dueDate,
                            setDate(
                                task.dueDate,
                                changeDate = { onTaskDoneChange(task.copy(dueDate = it)) })
                        )
                    }
                )
                Text(text = timeFormatter.format(task.dueDate),
                    Modifier.clickable {
                        showTimePicker(
                            context,
                            task.dueDate,
                            setTime(
                                task.dueDate,
                                changeTime = { onTaskDoneChange(task.copy(dueDate = it)) })
                        )
                    }
                )
            }
            AccountDivider()
            Column(Modifier.padding(itemPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Status", style = labelStyle)
                    if (task.done)
                        Text(text = "Completed On", style = labelStyle)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (task.done) "Complete" else "Incomplete",
                        modifier = Modifier.clickable {
                            onTaskDoneChange(
                                task.copy(
                                    done = !task.done,
                                    completedDate = if (task.done) null else Calendar.getInstance().time
                                )
                            )
                        }
                    )
                    if (task.done)
                        Text(
                            text = dateFormatter.format(
                                task.completedDate ?: Calendar.getInstance().time,
                            ),
                            modifier = Modifier
                                .clickable {
                                    showDatePicker(
                                        context = context,
                                        date = task.completedDate ?: Calendar.getInstance().time,
                                        setDate = setDate(
                                            date = task.completedDate
                                                ?: Calendar.getInstance().time,
                                            changeDate = {
                                                onTaskDoneChange(
                                                    task.copy(completedDate = it)
                                                )
                                            }
                                        )
                                    )
                                }
                        )
                }
            }
            AccountDivider()
            Column(Modifier.padding(itemPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Assigned To", style = labelStyle)
                    Text(text = "Created By", style = labelStyle)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val assignee = users.find { it.documentID == task.assignedTo } ?: User()
                    Text(
                        text = assignee.fullName(),
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { isAssigneeDialogShowing = true },
                    )
                    val creator = users.find { it.documentID == task.wasCreatedBy } ?: User()
                    Text(
                        text = creator.fullName(),
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable { isCreatorDialogShowing = true },
                        textAlign = TextAlign.End
                    )
                }
            }
            AccountDivider()
            Column(Modifier.padding(itemPadding)) {
                Text(text = "Estimated Costs", style = labelStyle)
                var estimatedCosts by remember { mutableStateOf(decimalFormat.format(task.estimatedCost)) }
                Row {
                    Text(text = "$ ")
                    BasicTextField(
                        value = estimatedCosts,
                        onValueChange = {
                            estimatedCosts = it
                            if (!estimatedCosts.isValidDoubleString(decimalFormat)) {
                                Toast.makeText(context, "Input a valid number", Toast.LENGTH_SHORT)
                                    .show()
                                return@BasicTextField
                            }
                            onTaskDoneChange(
                                task.copy(
                                    estimatedCost = decimalFormat.parse(estimatedCosts)
                                        ?.toDouble() ?: 0.0
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    )
                }
            }
            ClickableTextField(
                value = matter.title,
                onValueChange = { onTaskDoneChange( task.copy(matter = it)) },
                label = "Select Matter",
                modifier = Modifier.clickable { matterDialog = true }
            )
        }
    }
}

@Composable
fun CreatedByDialog(
    isCreatorDialogShowing: Boolean,
    onCreatorDialogDismissRequest: () -> Unit,
    creatorID: String,
    onCreatorSelected: (User) -> Unit,
    users: List<User>
) {
    if (isCreatorDialogShowing)
        Dialog(onCreatorDialogDismissRequest) {
            Column {
                LazyColumn(Modifier.padding(bottom = 60.dp)) {
                    items(users) { user ->
                        UserCardPicker(
                            user = user,
                            onSelected = onCreatorSelected,
                            onDismissRequest = onCreatorDialogDismissRequest
                        )
                    }
                }
            }
        }
}

@Composable
fun AssignedToDialog(
    isAssigneeDialogShowing: Boolean,
    onAssignmentDismissRequest: () -> Unit,
    assigneeID: String?,
    onAssigneeSelected: (User) -> Unit,
    users: List<User>
) {
    if (isAssigneeDialogShowing)
        Dialog(onAssignmentDismissRequest) {
            Column {
                LazyColumn(Modifier.padding(bottom = 60.dp)) {
                    items(users) { user ->
                        UserCardPicker(
                            user = user,
                            onSelected = onAssigneeSelected,
                            onDismissRequest = onAssignmentDismissRequest
                        )
                    }
                }
            }
        }

}

@Composable
fun UserCardPicker(
    user: User,
    onSelected: (User) -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelected(user)
                onDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {
            Text(
                text = user.fullName(),
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}
