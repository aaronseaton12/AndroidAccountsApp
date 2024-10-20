package com.aaronseaton.accounts.presentation.task

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import com.aaronseaton.accounts.presentation.components.*
import java.util.*

@Composable
fun AddTask(
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val addTask = { task: Task -> viewModel.addTask(task) }
    val state by viewModel.addTask.collectAsState()
    AddTaskImpl(
        state,
        addTask,
        navigateTo
    )
}


@Composable
fun AddTaskImpl(
    state: IndividualTaskState,
    addTask: (Task) -> Unit,
    navigateTo: (String) -> Unit
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { }
    val title = "Add or Edit Task"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) {

        when (state.loading) {
            true -> LoadingScreen()
            false -> AddTaskContent(
                state.accountUser,
                navigateTo,
                addTask,
                Modifier.padding(it)
            )
        }
    }
}

@Composable
fun AddTaskContent(
    accountUser: User,
    navigateTo: (String) -> Unit,
    addTask: (Task) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState(0)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current

        var task by remember {
            mutableStateOf(
                Task(
                    assignedTo = accountUser.documentID,
                    wasCreatedBy = accountUser.documentID
                )
            )
        }
        var amount by remember { mutableStateOf(decimalFormat.format(task.estimatedCost)) }

        val onChangeName = { name: String -> task = task.copy(name = name) }
        val onChangeDescription =
            { description: String -> task = task.copy(description = description) }

        val setDueDate = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newDueDate = Date(year - 1900, month, day, task.dueDate.hours, task.dueDate.minutes)
            task = task.copy(dueDate = newDueDate)
        }

        EditOrAddTextField(name = task.name, label = "Task Name", onTextChange = onChangeName)
        EditOrAddTextField(
            name = task.description,
            label = "Task Description",
            onTextChange = onChangeDescription
        )
        ClickableTextField(
            value = dateFormatter.format(task.dueDate),
            onValueChange = { },
            label = "Due Date",
            modifier = Modifier.clickable {
                showDatePicker(
                    context,
                    task.dueDate,
                    setDueDate
                )
            }
        )
        EditOrAddNumberField(amount, "Estimated Costs") {
            amount = it
            if (!amount.isValidDoubleString()) {
                Toast.makeText(context, "Input a valid number", Toast.LENGTH_SHORT).show()
                return@EditOrAddNumberField
            }
            task = task.copy(estimatedCost = amount.toDouble())
        }

        Button(
            onClick = {
                addTask(task)
                Toast.makeText(context, "Task Added", Toast.LENGTH_SHORT).show()
                navigateTo(Routes.TASK_LIST)
            }
        ) {
            Text(text = "Add Task")
        }
    }
}
