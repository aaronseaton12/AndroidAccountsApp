package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.format
import com.aaronseaton.accounts.util.webViewPrint
import com.aaronseaton.accounts.util.emailTransaction
import com.aaronseaton.accounts.util.fileLocation
import com.aaronseaton.accounts.util.fileName
import com.aaronseaton.accounts.util.htmlStringThree
import com.aaronseaton.accounts.util.saveHTMLAsPDF
import com.aaronseaton.accounts.util.tailWindStyle


@Composable
fun IndividualTransaction(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier
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
    val fileName = fileName(customer, transaction)
    val fileLocation = fileLocation(fileName)
    val htmlString = htmlStringThree(tailWindStyle, customer, business, accountUser, transaction)
    val saveTransactionAsPDF = {
        saveHTMLAsPDF(
            context,
            htmlString,
            fileLocation,
        )
    }
    val emailTransaction = {
        emailTransaction(
            customer,
            transaction,
            saveTransactionAsPDF,
            context,
            fileLocation,
        )
    }
    val printTransaction = {
        webViewPrint(
            context,
            htmlString,
            fileName,
        )
    }
    val editTransactionRoute = when (transaction) {
        is Receipt -> Routes.EDIT_RECEIPT
        is Payment -> Routes.EDIT_PAYMENT
        else -> "Transaction"
    }
    val labelStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
    )
    val valueStyle = MaterialTheme.typography.titleSmall
    val labelModifier = Modifier.padding(bottom = 10.dp)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Amount",
                    textAlign = TextAlign.Left,
                    style = labelStyle,
                    modifier = labelModifier
                )
                Text(
                    "$${transaction.amount.format()}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme
                        .typography
                        .titleLarge
                        .copy(fontWeight = FontWeight.SemiBold)
                )
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Contact",
                    textAlign = TextAlign.Left,
                    style = labelStyle,
                    modifier = labelModifier
                )

                Text(
                    customer.fullName(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.clickable {
                        navigateTo(Routes.INDIVIDUAL_CUSTOMER + "/" + transaction.customerID)
                    }
                )
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Date",
                    textAlign = TextAlign.Left,
                    style = labelStyle,
                    modifier = labelModifier
                )

                Text(
                    Util.prettyDateFormatter.format(transaction.date),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            tonalElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Reason",
                    textAlign = TextAlign.Left,
                    style = labelStyle,
                    modifier = labelModifier
                )

                transaction.reason?.let {
                    Text(
                        it,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8F)
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = printTransaction,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                shape = MaterialTheme.shapes.small,
            ) {
                Text("Print $transactionName")
            }
            TextButton(
                onClick = emailTransaction,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Email $transactionName")
            }
            TextButton(
                onClick = { navigateTo(editTransactionRoute + "/${transaction.documentID}") },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Edit")
            }
        }
    }
}
