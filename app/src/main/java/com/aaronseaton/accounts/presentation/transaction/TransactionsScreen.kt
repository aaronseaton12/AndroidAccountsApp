package com.aaronseaton.accounts.presentation.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.util.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen (navigateTo: (String) -> Unit, modifier: Modifier = Modifier) {
    val icon = Icons.AutoMirrored.Filled.ArrowBack
    val onPressed = { navigateTo(Routes.HOME) }
    val description = "Home"
    val title = "Transactions"
    Scaffold (
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
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.TRANSACTION_SCREEN) },
    ) {
        Column(modifier.fillMaxSize().padding(it)) {
            Box(
                modifier = modifier
                    .padding(vertical = 3.dp)
                    .clickable { navigateTo(Routes.RECEIPT_LIST) },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    Modifier
                        .fillMaxWidth(),
                    tonalElevation = 1.dp,
                ) {
                    Column(Modifier.padding(vertical = 30.dp, horizontal = 10.dp)) {
                        Text(
                            text = "Receipt List",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "List of Money Received from clients")
                    }
                }
            }
            Box(
                modifier = modifier
                    .padding(vertical = 3.dp)
                    .clickable { navigateTo(Routes.PAYMENT_LIST) },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    Modifier
                        .fillMaxWidth(),
                    tonalElevation = 1.dp,
                ) {
                    Column(Modifier.padding(vertical = 30.dp, horizontal = 10.dp)) {
                        Text(
                            text = "Payments List",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "List of Money Paid Out to vendors")
                    }
                }
            }
        }
    }
}