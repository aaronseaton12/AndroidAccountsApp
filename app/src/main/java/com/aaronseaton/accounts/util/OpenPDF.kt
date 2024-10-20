package com.aaronseaton.accounts.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Receipt
import java.io.File

const val TAG = "Open PDF TAG"
fun openPDF(
    context: Context,
    fileLocation: File,
) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri = FileProvider.getUriForFile(
            context, "com.aaronseaton.accounts.provider",
            fileLocation
        )
        intent.setDataAndType(contentUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ContextCompat.startActivity(context, intent, null)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "There is no PDF Viewer", Toast.LENGTH_SHORT).show()
    }
}

fun openHTML(
    transaction: FinancialTransaction,
    context: Context
) {
    val fileDirectory = if (transaction is Receipt) "Receipt_" else "Payment_"
    val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val htmlFile = File(folder, "$fileDirectory${transaction.documentID.take(4)}.html")
    try {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val contentUri = FileProvider.getUriForFile(
            context, "com.aaronseaton.accounts.provider",
            htmlFile
        )
        intent.setDataAndType(contentUri, "text/HTML")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ContextCompat.startActivity(context, intent, null)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "There is no HTML Viewer", Toast.LENGTH_SHORT).show()
    }
}