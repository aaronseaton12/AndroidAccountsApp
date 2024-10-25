package com.aaronseaton.accounts.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(apiLevel = 33,)
@Preview(apiLevel = 33, name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Large screen", device = Devices.PIXEL_C)
@Composable
private fun EditPaymentPreview() {
    AccountsTheme {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = { Text("Presenting") },
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon( Icons.AutoMirrored.Filled.ArrowBack,null)
                        }},
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Call, null)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.Email, null)
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                    }
                )
            }
        ) {
            Column (Modifier.padding(it)) {
                Text("This")

            }
        }
    }
}
