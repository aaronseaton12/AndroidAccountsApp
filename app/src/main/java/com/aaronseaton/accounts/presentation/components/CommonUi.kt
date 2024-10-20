package com.aaronseaton.accounts.presentation.components

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import java.util.*

@Composable
fun AccountIndicator(color: Color, modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .size(4.dp, 50.dp)
            .background(color = color)
    )
}

@Composable
fun AccountDivider(
    color: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
) {
    Divider(
        modifier = Modifier.padding(horizontal = 10.dp),
        thickness = 1.dp,
        color = color
    )
}

@Composable

fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
//        LinearProgressIndicator(
//            modifier = modifier.fillMaxWidth().padding(top = 64.dp),
//            trackColor = MaterialTheme.colorScheme.primaryContainer,
//            color = MaterialTheme.colorScheme.primary,
//
//        )
        CircularProgressIndicator(
            modifier = Modifier
                .size(130.dp, 130.dp)
                .align(Alignment.Center),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Loading...",
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}


fun showDatePicker(
    context: Context,
    date: Date,
    setDate: DatePickerDialog.OnDateSetListener
) {
    val calendar = Calendar.getInstance()
    calendar.time = date
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val day = calendar[Calendar.DAY_OF_MONTH]
    val datePickerDialog = DatePickerDialog(
        context, setDate, year, month, day
    )
    datePickerDialog.show()
}

fun showTimePicker(
    context: Context,
    date: Date,
    setTime: TimePickerDialog.OnTimeSetListener
) {
    val calendar = Calendar.getInstance()
    calendar.time = date
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]
    val timePickerDialog = TimePickerDialog(
        context, setTime, hour, minute, false
    )
    timePickerDialog.show()
}

@Composable
fun CompoundFAB(
    modifier: Modifier = Modifier,
    fabLabel: String,
    fabIcon: ImageVector,
    onTopFabPressed: () -> Unit,
    onBottomFabPressed: () -> Unit,
    topFabLabel: String,
    bottomFabLabel: String,
) {
    var showThreeButtons by remember { mutableStateOf(false) }
    val fabModifier = modifier
        .width(160.dp)
        .padding(2.dp)
    Column {
        if (showThreeButtons)
            ExtendedFloatingActionButton(
                text = { Text(topFabLabel) },
                icon = { Icon(Icons.Default.Create, null) },
                onClick = onTopFabPressed,
                modifier = fabModifier.align(Alignment.Start)
            )
        if (showThreeButtons)
            ExtendedFloatingActionButton(
                text = { Text(bottomFabLabel) },
                icon = { Icon(Icons.Default.AddCircle, null) },
                onClick = onBottomFabPressed,
                modifier = fabModifier.align(Alignment.Start)
            )
        ExtendedFloatingActionButton(
            text = { Text(fabLabel) },
            icon = { Icon(fabIcon, fabLabel) },
            onClick = { showThreeButtons = !showThreeButtons },
            modifier = fabModifier.align(Alignment.Start)
        )
    }
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun LoadingScreenPreview() {
    AccountsTheme {
        Scaffold(
            topBar = {
                AllTopAppBar(
                    title = "Title",
                    leftIcon = Icons.Default.Home,
                    onLeftIcon = { /*TODO*/ })
            },
            bottomBar = {
                AllBottomBar(navigateTo = {})
            }
        ) { padding ->
            LoadingScreen()
        }
    }
}