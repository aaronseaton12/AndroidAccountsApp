package com.aaronseaton.accounts.presentation.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat
import com.aaronseaton.accounts.util.Util.Companion.format
import com.aaronseaton.accounts.util.emailTransaction
import com.aaronseaton.accounts.util.fileLocation
import com.aaronseaton.accounts.util.fileName
import com.aaronseaton.accounts.util.htmlStringThree
import com.aaronseaton.accounts.util.saveHTMLAsPDF
import com.aaronseaton.accounts.util.tailWindStyle
import com.aaronseaton.accounts.util.webViewPrint


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun TransactionCardPreview() {
    AccountsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.surface,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Add Income") },
                    icon = { Icon(Icons.Default.Add, "Add Income") },
                    onClick = { }
                )
            }
        ) {
            TransactionCard(
                transaction = TestInfo.firstReceipt,
                customer = TestInfo.Damian,
                accountUser = User(),
                business = Business(),
                navigateTo = {},
            )
        }

    }
}

@Composable
fun TransactionCard(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TransactionContents(
        transaction = transaction,
        customer = customer,
        accountUser = accountUser,
        business = business,
        navigateTo = navigateTo,
        modifier = modifier,
    )
}

@Composable
fun TransactionContentsTwo(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
){
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
    val editTransactionRoute = when (transaction) {
        is Receipt -> Routes.EDIT_RECEIPT
        is Payment -> Routes.EDIT_PAYMENT
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
            fileName
        )
    }
    val labelStyle = MaterialTheme.typography.bodySmall.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
    )
    val valueStyle = MaterialTheme.typography.titleSmall
    val labelModifier = Modifier.padding(bottom = 10.dp)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { navigateTo(individualTransactionRoute + "/" + transaction.documentID) },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 1.dp,
            //shadowElevation = 3.dp,
            shape = MaterialTheme.shapes.small,
            //border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(

                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(
                        vertical = 8.dp,
                        horizontal = 4.dp
                    )
                    .widthIn(max = 450.dp)
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "$${transaction.amount.format()}",
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = customer.fullName(),
                        textAlign = TextAlign.Center,
                        style = labelStyle,
                        //modifier = labelModifier
                    )
                    Text(
                        text = transaction.reason ?: "",
                        textAlign = TextAlign.Left,
                        style = labelStyle,
                        modifier = labelModifier.padding(top = 0.dp)
                    )
                    //AccountDivider()
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = dateFormatter.format(transaction.date),
                                textAlign = TextAlign.Left,
                                style = valueStyle
                            )
                            Text(
                                text = "Dated",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }
                        Column {
                            Text(
                                text = transaction.payMethod ?: "-",
                                textAlign = TextAlign.Left,
                                style = valueStyle
                            )
                            Text(
                                text = "Method",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }
                        Column {
                            Text(
                                text = "$transactionName ‧ ${transaction.documentID.take(4)}",
                                textAlign = TextAlign.Center,
                                style = valueStyle
                            )

                            Text(
                                text = "Amount",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }

                    }
                }
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
    }
}

@Composable
fun TransactionContents(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
//    val context = LocalContext.current
//    val transactionName = when (transaction) {
//        is Receipt -> "Receipt"
//        is Payment -> "Payment"
//        else -> "Transaction"
//    }
    val individualTransactionRoute = when (transaction) {
        is Receipt -> Routes.INDIVIDUAL_RECEIPT
        is Payment -> Routes.INDIVIDUAL_PAYMENT
        else -> "Transaction"
    }
//    val editTransactionRoute = when (transaction) {
//        is Receipt -> Routes.EDIT_RECEIPT
//        is Payment -> Routes.EDIT_PAYMENT
//        else -> "Transaction"
//    }
//    val fileName = fileName(customer, transaction)
//    val fileLocation = fileLocation(fileName)
//    val htmlString = htmlStringThree(tailWindStyle, customer, business, accountUser, transaction)
//    val saveTransactionAsPDF = {
//        saveHTMLAsPDF(
//            context,
//            htmlString,
//            fileLocation,
//        )
//    }
//    val emailTransaction = {
//        emailTransaction(
//            customer,
//            transaction,
//            saveTransactionAsPDF,
//            context,
//            fileLocation,
//        )
//    }
//    val printTransaction = {
//        webViewPrint(
//            context,
//            htmlString,
//            fileName
//        )
//    }
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.8
    )
    val valueStyle = MaterialTheme.typography.titleMedium
    val labelModifier = Modifier.padding(bottom = 10.dp)
    val lightness = if (isSystemInDarkTheme()) 0.7F else 0.45F
    val incomeColor = Color.hsl(
        133F,
        0.1F,
        lightness,
        1.0F,
        ColorSpaces.AdobeRgb
    )
    val expenseColor = Color.hsl(
        0F,
        0.3F,
        lightness,
        1.0F,
        ColorSpaces.AdobeRgb
    )
    val transactionColor = if(transaction is Receipt) incomeColor else expenseColor


    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 3.dp)
            .clickable { navigateTo(individualTransactionRoute + "/" + transaction.documentID) },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 0.25.dp,
            border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Row( modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp) ) {
                Column(
                    modifier = Modifier.weight(2f).padding(end = 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = customer.fullName(),
                        style = valueStyle.copy(
                            lineHeight = MaterialTheme.typography.titleMedium.lineHeight * 0.8
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateFormatter.format(transaction.date),
                        style = labelStyle
                    )
                }
                Column(
                    modifier = Modifier.weight(3f),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = (if(transaction is Payment) "-" else "") +
                                "$${decimalFormat.format(transaction.amount)}",
                        style = valueStyle.copy(color = transactionColor )
                    )
                    transaction.reason?.let { Text(
                        text = it,
                        style = labelStyle
                        //style = MaterialTheme.typography.bodySmall
                    ) }
                }
            }
        }
    }
}

@Composable
fun TransactionContentsThree(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit = {},
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
    val editTransactionRoute = when (transaction) {
        is Receipt -> Routes.EDIT_RECEIPT
        is Payment -> Routes.EDIT_PAYMENT
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
            fileName
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { navigateTo(individualTransactionRoute + "/" + transaction.documentID) },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 1.dp,
            shadowElevation = 3.dp,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$transactionName - ${transaction.documentID.take(4)} - " +
                            customer.fullName(),
                    textAlign = TextAlign.Justify,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 15.dp, bottom = 15.dp)
                )
                AccountDivider()
                Row(
                    Modifier.padding(start = 30.dp, end = 30.dp)
                ) {
                    Text(
                        "Dated: \n" +
                                "Method: \n" +
                                "Amount: \n" +
                                "Reason: \n",
                        modifier = Modifier.padding(5.dp),
                        textAlign = TextAlign.Right
                    )
                    Text(//"${documentID.take(4)}\n" +
                        //"${customer.fullName()}\n" +
                        "${dateFormatter.format(transaction.date)}\n" +
                                "${transaction.payMethod ?: "-"}\n" +
                                "$${decimalFormat.format(transaction.amount)}\n" +
                                transaction.reason,
                        modifier = Modifier.padding(5.dp),
                        textAlign = TextAlign.Left,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilledTonalButton(
                        onClick = printTransaction,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Print $transactionName")
                    }
                    FilledTonalButton(
                        onClick = emailTransaction,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Email $transactionName")
                    }
                    FilledTonalButton(
                        onClick = { navigateTo(editTransactionRoute + "/${transaction.documentID}") },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}
