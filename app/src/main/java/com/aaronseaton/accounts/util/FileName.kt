package com.aaronseaton.accounts.util

import android.os.Environment
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import java.io.File

fun fileName(
    customer: Customer,
    transaction: FinancialTransaction,
): String {
    val transName = transaction.transactionName()
    val fullName = customer.fullName()
    val shortID = transaction.documentID.take(4)
    val date = Util.dateFormatter.format(transaction.date)
    return "${transName}_${fullName}_${date}_${shortID}.pdf"
}

fun fileLocation (
    fileName: String,
    folder: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
): File {
    return File(folder, fileName)
}