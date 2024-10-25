package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.ui.theme.AccountsTheme

@Composable
fun HeaderComponent(
    modifier: Modifier = Modifier,
    title: String? = null,
    actionIcon: @Composable ()->Unit,
    menuItems: @Composable ColumnScope.()-> Unit,
    contents: @Composable ColumnScope.()-> Unit,
){
    var expanded by remember { mutableStateOf(false) }
    Row(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.weight(10.0f)
        ) {
            title?.let {
                Text(
                    text = it,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,

                    ),
                )
            }
            contents()
        }

        Column(Modifier.weight(1.0f)) {
            IconButton(
                onClick = { expanded = !expanded }) {
                actionIcon()
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems()
            }
        }
    }
}

@Preview(apiLevel = 33)
@Composable
fun HeaderPreview() {
    val modifier = Modifier
    val title = "Test Title"
    @Composable fun contents() {
        Text("Text 1")
        Text("Text 3")
    }
    @Composable fun actionIcon () {
        Icon (Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
    }
    @Composable fun menuItems () {
        DropdownMenuItem(text = { Text("Edit") }, onClick = { })
    }
    AccountsTheme {
        var expanded by remember { mutableStateOf(false) }
        Surface(tonalElevation = 1.dp) {
            Row(modifier = modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.weight(10.0f)
                ) {
                    Row {
                        Text(
                            text = title,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                        )

                    }
                    contents()
                }

                Surface(Modifier.weight(1.0f)) {
                    IconButton(
                        modifier = Modifier.padding(0.dp),
                        onClick = { expanded = !expanded }) {
                        actionIcon()
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        menuItems()
                    }
                }
            }
        }
    }
}