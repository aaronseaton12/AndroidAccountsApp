package com.aaronseaton.accounts.presentation.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.presentation.components.AccountDivider
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util

@Composable
fun TaskCard(
    task: Task,
    user: User = User(),
    navigateTo: (String) -> Unit,
    updateTask: (Task) -> Unit
) {
    Row (
        Modifier
            .fillMaxWidth()
            .clickable { navigateTo(Routes.INDIVIDUAL_TASK + "/" + task.documentID) }
    ) {
        Checkbox(
            checked = task.done,
            onCheckedChange = { updateTask(task.copy(done = !task.done)) }
        )
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp)
        ) {
            Text(
                text = task.name,
                modifier = Modifier.fillMaxWidth(),
                style = if (!task.done) MaterialTheme.typography.titleMedium else
                    MaterialTheme.typography.titleMedium.copy(
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
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
            )
            Text(
                text = Util.prettyDateFormatter.format(task.dueDate),
                modifier = Modifier.fillMaxWidth(),
                style = if (!task.done) MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ) else
                    MaterialTheme.typography.bodyMedium.copy(
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
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
            )
        }
    }
    AccountDivider()
}

@Preview (apiLevel = 33)
@Composable
fun TaskCardPreview() {
    AccountsTheme {
        TaskCard(
            task = Task(
                documentID = "AaO",
                name = "This",
                description = "This description",
            ),
            navigateTo = {},
            updateTask = {}
        )
    }
}