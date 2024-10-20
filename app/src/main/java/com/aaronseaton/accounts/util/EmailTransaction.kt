package com.aaronseaton.accounts.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Receipt
import java.io.File

fun emailTransaction(
    customer: Customer,
    transaction: FinancialTransaction,
    saveTransactionAsPDF: () -> Unit,
    context: Context,
    fileLocation: File,
) {
    if(!fileLocation.exists()){
        saveTransactionAsPDF()
    }
    val subjectPayment =
        "Payment Voucher ${transaction.documentID.dropLast(16)} for ${customer.firstName} ${customer.lastName}"
    val bodyPayment =
        "Dear ${customer.firstName} ${customer.lastName}, \n\n Please see the attached " +
                "payment voucher for funds paid to you on ${Util.dateFormatter.format(transaction.date)}. \n" +
                "\n Thank you for your continued " +
                "trust and confidence in us!"

    val subjectReceipt =
        "Receipt ${transaction.documentID.dropLast(16)} for ${customer.firstName} ${customer.lastName}"
    val bodyReceipt =
        "Dear ${customer.firstName} ${customer.lastName}, \n\n Please see the attached " +
                "receipt for funds received from you on ${Util.dateFormatter.format(transaction.date)}. \n" +
                "\n Thank you for your continued " +
                "trust and confidence in us!"
    val subject = if (transaction is Receipt) subjectReceipt else subjectPayment
    val body = if (transaction is Receipt) bodyReceipt else bodyPayment

    val email = arrayOf(customer.emailAddress)
    val attachment = FileProvider.getUriForFile(
        context, "com.aaronseaton.accounts.provider",
        fileLocation
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, email)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        putExtra(Intent.EXTRA_STREAM, attachment)
    }
    ContextCompat.startActivity(context, intent, null)
}