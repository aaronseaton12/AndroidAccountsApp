package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun AllFloatingActionButton(
    onClick: ()-> Unit,
    icon: ImageVector = Icons.Filled.Add,
    fabText: String = ""
) {
    FloatingActionButton(onClick = onClick) {
        Row {
            Text(fabText)
            Icon(icon, null)
        }
    }
}