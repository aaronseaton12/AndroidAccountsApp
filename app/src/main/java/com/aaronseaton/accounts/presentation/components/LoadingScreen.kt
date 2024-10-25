package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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