package com.aaronseaton.accounts.presentation.receipt

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.presentation.components.AccountDivider
import com.aaronseaton.accounts.presentation.components.TransactionCard

@Composable
fun ReceiptCard(
    customer: Customer,
    receipt: Receipt,
    navigateTo: (String) -> Unit = {}
) {
    val (_, date, amount, _) = receipt
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateTo(Routes.INDIVIDUAL_RECEIPT + "/" + receipt.documentID) },
        tonalElevation = 0.dp
    ) {
        Column {
            Row(Modifier.padding(15.dp, 10.dp)) {
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
        AccountDivider()
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
            navigateTo = {},
            business = Business(),
            accountUser = User()
        )
    }
}


/*
@Composable
fun TransactionCard(
    transaction: FinancialTransaction,
    customer: Customer,
    navigateTo: (String) -> Unit = {},
    saveTransactionAsPDF: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val transactionName = when (transaction) {
        is Receipt -> "Receipt"
        is Payment -> "Payment"
        else -> "Transaction"
    }
    val individualTransactionRoute = when (transaction) {
        is Receipt -> Routes.INDIVIDUAL_RECEIPT
        is Payment -> Routes.INDIVIDUAL_PAYMENT
        else -> "Transaction"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .clickable { navigateTo(individualTransactionRoute + "/" + transaction.documentID) },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 0.dp,
            shadowElevation = 2.dp,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                //modifier = Modifier.size(300.dp, 400.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "$transactionName for  ${customer.fullName()}",
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp).align(Alignment.CenterHorizontally)
                )
                AccountDivider()
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 0.dp).fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 10.dp)
                    ){
                        TextWithLabel("Amount",Util.decimalFormat.format(transaction.amount))
                        TextWithLabel("Dated", Util.dateFormatter.format(transaction.date))
                        TextWithLabel("Method", transaction.payMethod ?: "-")
                    }
//                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
//                        TextWithLabel("Ref#", transaction.documentID.take(4))
//                    }
                    TextWithLabel("Reason", transaction.reason ?: "-")
                }

                //
                val emailReceipt = {
                    emailReceipt(
                        customer,
                        transaction,
                        saveTransactionAsPDF,
                        context
                    )
                }
                val emailTransaction = {
                    emailTransaction(
                        customer,
                        transaction,
                        saveTransactionAsPDF,
                        context
                    )
                }
                val random = { println("Email Try") }

                val emailTransaction: () -> Unit = when (transaction) {
                    is Receipt -> emailReceipt
                    is Payment -> emailTransaction
                    else -> random
                }
                val editTransactionRoute = when (transaction) {
                    is Receipt -> Routes.EDIT_RECEIPT
                    is Payment -> Routes.EDIT_PAYMENT
                    else -> "Transaction"
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ElevatedButton(onClick = {}) { Text("Print $transactionName") }
                    ElevatedButton(onClick = emailTransaction)
                    { Text("Email $transactionName") }
                    ElevatedButton(onClick = { navigateTo(editTransactionRoute + "/${transaction.documentID}") })
                    { Text("Edit") }
                }
            }
        }
    }
}

 */