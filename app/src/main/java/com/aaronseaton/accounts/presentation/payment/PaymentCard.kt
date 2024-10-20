package com.aaronseaton.accounts.presentation.payment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.presentation.components.AccountDivider

@Composable
fun PaymentCard(
    customer: Customer,
    payment: Payment,
    navigateTo: (String) -> Unit = {},
) {
    val (_, date, amount, customerID) = payment

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateTo(Routes.INDIVIDUAL_PAYMENT + "/" + payment.documentID) },
        tonalElevation = 0.dp
    ) {
        Column {
            Row(Modifier.padding(15.dp, 10.dp)) {
                //AccountIndicator(Color(180, 30, 30, 200))
                Column(Modifier.fillMaxWidth(0.55f)) {
                    Text(
                        text = "Payment: ${payment.documentID.take(4)}",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Left
                    )
                    Text(
                        text = "${customer.firstName} ${customer.lastName}",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Left
                    )
                }
                Column {
                    Text(
                        text = "$${Util.decimalFormat.format(amount)}",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = Util.dateFormatter.format(date),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
        AccountDivider()
    }
}