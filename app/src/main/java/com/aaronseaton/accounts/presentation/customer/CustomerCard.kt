package com.aaronseaton.accounts.presentation.customer

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AccountDivider

@Composable
fun CustomerCard(
    customer: Customer,
    navigateTo: (String) -> Unit = {}
) {
    val (_, firstName, middleName, lastName, email, address, phone) = customer
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("Sending Customer ${customer.documentID}")
                navigateTo(Routes.INDIVIDUAL_CUSTOMER + "/${customer.documentID}")
            },
        tonalElevation = 0.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = customer.fullName(),
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                textAlign = TextAlign.Left
            )
            Text(
                text = email.ifBlank { "Please add email" },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "$address",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "$phone",
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Preview
@Composable
fun CustomerCardPreview (){
    Scaffold {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            CustomerCard(customer = TestInfo.Khadija)
            CustomerCard(customer = TestInfo.Damian)
            CustomerCard(customer = TestInfo.Khadija)
            CustomerCard(customer = TestInfo.Khadija)
            CustomerCard(customer = TestInfo.Damian)
            CustomerCard(customer = TestInfo.Khadija)
            CustomerCard(customer = TestInfo.Damian)
        }
    }
}