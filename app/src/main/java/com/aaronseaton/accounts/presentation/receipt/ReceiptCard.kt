package com.aaronseaton.accounts.presentation.receipt

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.presentation.components.TransactionCard
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util

@Composable
fun ReceiptCard(
    customer: Customer,
    receipt: Receipt,
    navigateTo: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (_, date, amount, _) = receipt
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateTo(Routes.INDIVIDUAL_RECEIPT + "/" + receipt.documentID) },
        tonalElevation = 0.dp
    ) {
        Column (modifier) {
            Row {
                Column(Modifier.fillMaxWidth(0.55f)) {
                    Text(
                        text = "Receipt: ${receipt.documentID.take(4)}",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        textAlign = TextAlign.Left
                    )
                    Text(
                        text = "${customer.firstName} ${customer.lastName}",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
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
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    }
}

@Composable
fun TextWithLabel(
    modifier: Modifier = Modifier,
    label: String,
    text: String
) {
    val labelStyle = MaterialTheme.typography.labelMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )
    val bigTextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onBackground
    )
    Column(modifier) {
        Text(text = label, style = labelStyle)
        Text(text = text, style = bigTextStyle)
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun ReceiptCardPreview() {
    AccountsTheme {
        TransactionCard(
            transaction = TestInfo.firstReceipt,
            customer = TestInfo.Damian,
            navigateTo = {string-> Log.d(TAG, string)},
            business = Business(),
            accountUser = User()
        )
    }
}