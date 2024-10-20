package com.aaronseaton.accounts.presentation.business

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AccountDivider

@Composable
fun BusinessCard(
    business: Business,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("Sending Customer ${business.documentID}")
                navigateTo(Routes.INDIVIDUAL_BUSINESS + "/" + business.documentID)
            },
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = business.name,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                textAlign = TextAlign.Left
            )
            Text(
                text = business.emailAddress.ifBlank { "Please add email" },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "${business.address}",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "${business.phoneNumber}",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            //Spacer(modifier = Modifier.padding(vertical = 5.dp))

        }
        AccountDivider()
    }
}
