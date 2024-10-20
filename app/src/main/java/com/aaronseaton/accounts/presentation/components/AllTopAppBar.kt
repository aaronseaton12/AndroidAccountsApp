package com.aaronseaton.accounts.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.ui.theme.AccountsTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTopAppBar(
    title: String,
    leftIcon: ImageVector,
    onLeftIcon: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    description: String = "Home",
) {
    Surface(tonalElevation = 1.dp) {
        CenterAlignedTopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onLeftIcon) {
                    Icon(leftIcon, description)
                }
            },
            actions = actions,

            )
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large screen", device = Devices.PIXEL_C)
@Composable
private fun EditPaymentPreview() {
    AccountsTheme {
        AllTopAppBar(
            title = "Presenting",
            leftIcon = Icons.Default.Add,
            onLeftIcon = { /*TODO*/ }
        )
    }
}
