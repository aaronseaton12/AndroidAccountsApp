package com.aaronseaton.accounts.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import java.util.Calendar
import java.util.Date

@Composable
fun AccountDivider(
    color: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
) {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 10.dp),
        thickness = 1.dp,
        color = color
    )
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


@Preview(apiLevel = 33)
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
            LoadingScreen(Modifier.padding(padding))
        }
    }
}