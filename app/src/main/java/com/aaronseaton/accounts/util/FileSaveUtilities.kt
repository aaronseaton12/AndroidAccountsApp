package com.aaronseaton.accounts.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PdfPrint
import android.print.PrintAttributes
import android.print.pdf.PrintedPdfDocument
import android.provider.DocumentsContract
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat.startActivityForResult
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt
import com.aaronseaton.accounts.util.PageSize as PageSize1

fun saveHTMLAsPDF(
    context: Context,
    htmlString: String,
    fileLocation: File,
){
    val webView = WebView(context)
    webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
        override fun onPageFinished(view: WebView, url: String) {
            Log.i(TAG, "page finished loading $url")
            webviewToPdf(webView, fileLocation)
        }
    }
    webView.loadDataWithBaseURL(null, htmlString, "text/HTML", "UTF-8", null)
}

private fun webviewToPdf(
    webView: WebView,
    fileLocation: File,
) {
    val vMargin = 0
    val hMargin = 0
    val attributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
        .setMinMargins(PrintAttributes.Margins(hMargin, vMargin, hMargin, vMargin))
        .build()
    val printer = PdfPrint(attributes)
    val printAdapter = webView.createPrintDocumentAdapter(fileLocation.name)
    printer.print(printAdapter, fileLocation)
}

fun screenshot(
    webView: WebView,
    fileLocation: File,
    width: Int,
    height:Int
) {
    val scaledBitmap = webViewToScaledBitMap(webView, width, height)
    val document = PdfDocument()
    val pageNumber = 1
    val pageInfo = PdfDocument
        .PageInfo
        .Builder(width, height, pageNumber)
        .create()
    val page = document.startPage(pageInfo)
    try {
        page.canvas.drawBitmap(scaledBitmap, 0F, 0F, null)
        document.finishPage(page)
    } catch (error: Exception) {
        error.printStackTrace()
    }
    savePdfToFile(document, fileLocation)
    document.close()
}

fun webViewToScaledBitMap(
    webView: WebView,
    width: Int,
    height:Int
): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    webView.draw(canvas)
    return Bitmap.createScaledBitmap(bitmap, width, height, true)
}

fun savePdfToFile(
    document: PdfDocument,
    fileLocation: File
) {
    try {
        document.writeTo(FileOutputStream(fileLocation))
    } catch (error: IOException) {
        error.printStackTrace()
    }
}

// Request code for creating a PDF document.
const val CREATE_FILE = 1

private fun createFile(activity: Activity, pickerInitialUri: Uri) {
    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/pdf"
        putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker before your app creates the document.
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
    }
    startActivityForResult(activity, intent, CREATE_FILE, null)
}
fun saveTransactionAsPdf(
    customer: Customer,
    transaction: FinancialTransaction,
    accountUser: User = User(),
    business: Business = Business(),
    signature: Drawable,
    businessLogo: Drawable
) {
    Log.d("INDIVIDUAL", "In Save Function")
    Log.d("Customer", "$customer")
    val document = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(612, 792, 1).create()
    val page = document.startPage(pageInfo)
    page.canvas.drawReceipt(
        businessLogo,
        business,
        accountUser,
        transaction,
        customer,
        signature
    )
    document.finishPage(page)
    val fileName = fileName(customer, transaction)
    val fileLocation = fileLocation(fileName)
    savePdfToFile(document, fileLocation)
    document.close()
}

private fun Canvas.drawReceipt(
    businessLogo: Drawable,
    business: Business,
    accountUser: User,
    transaction: FinancialTransaction,
    customer: Customer,
    signature: Drawable
) {
    val blackPaint = Paint().apply {
        color = Color.BLACK
        textSize = 14f
    }
    val greyPaint = Paint().apply {
        color = Color.GRAY
        textSize = 14f
    }
    val blackPaintHeader = Paint().apply {
        color = Color.GRAY
        typeface = Typeface.DEFAULT
        textSize = 8f
        textAlign = Paint.Align.RIGHT
    }
    businessLogo.setBounds(50, 50, 150, 150)
    businessLogo.draw(this)
    drawText(business.name, 550F, 50F, Paint().apply {
        textSize = 10f
        typeface = Typeface.SANS_SERIF
        textAlign = Paint.Align.RIGHT
    })
    val space = 9F
    val position = 60F
    drawText(accountUser.address.addressLine1, 550F, position + 1 * space, blackPaintHeader)
    drawText(accountUser.address.addressLine2, 550F, position + 2 * space, blackPaintHeader)
    drawText(accountUser.address.city, 550F, position + 3 * space, blackPaintHeader)
    drawText(accountUser.address.country, 550F, position + 4 * space, blackPaintHeader)
    drawText(
        "M: ${accountUser.phoneNumber.cellNumber}",
        550F,
        position + 5 * space,
        blackPaintHeader
    )
    drawText(
        "O: ${accountUser.phoneNumber.workNumber}",
        550F,
        position + 6 * space,
        blackPaintHeader
    )
    drawText("E: ${accountUser.emailAddress}", 550F, position + 7 * space, blackPaintHeader)
    val transactionName = if (transaction is Receipt) "Receipt" else "Payment"
    drawText(transactionName, 280F, 200F, Paint().apply {
        textSize = 24f
        isFakeBoldText = true
    })

    drawText("Date Paid", 40F, 248F, greyPaint)
    drawText(dateFormatter.format(transaction.date), 125F, 248F, blackPaint)
    drawLine(125F, 250F, 300F, 250F, blackPaint)//datepaid

    drawText(if (transaction is Receipt) "Paid By" else "Paid To", 40F, 280F, greyPaint)
    drawText("${customer.firstName} ${customer.lastName}", 125F, 278F, blackPaint)
    drawLine(125F, 280F, 300F, 280F, blackPaint)//paidby

    drawText("Reason", 40F, 310F, greyPaint)
    drawText("${transaction.reason}", 125F, 308F, blackPaint)
    drawLine(125F, 310F, 550F, 310F, blackPaint)//matter

    drawText(if (transaction is Receipt) "Receipt No." else "Payment No", 320F, 248F, greyPaint)
    drawText(transaction.documentID.take(4), 400F, 248F, blackPaint)
    drawLine(400F, 250F, 550F, 250F, blackPaint)//receiptNo

    drawText("Amount", 320F, 278F, greyPaint)
    drawText("$${decimalFormat.format(transaction.amount)}", 400F, 278F, blackPaint)
    drawLine(400F, 280F, 550F, 280F, blackPaint)//Amount

    drawText("Signature", 320F, 350F, greyPaint)
    signature.setBounds(400, 320, 500, 350)
    signature.draw(this)
    drawLine(400F, 350F, 550F, 350F, blackPaint)//Signature
}

fun saveTransactionAsHTML(
    customer: Customer,
    transaction: FinancialTransaction,
    accountUser: User = User(),
    business: Business = Business(),
    folder: File,
    html: String
) {
    val transactionName = transaction.transactionName()
    val htmlString = htmlStringTwo(styleStringThree, customer, business, accountUser, transaction)
    val htmlFile = File(folder, "${transactionName}_${transaction.documentID.take(4)}.html")
    val output = FileOutputStream(htmlFile)
    try {
        output.write(htmlString.toByteArray())
    } catch (error: IOException) {
        error.printStackTrace()
    }
}

private fun createWebPrintJob2(
    webView: WebView,
    context: Context,
    transaction: FinancialTransaction,
    customer: Customer,
    pageSize: PageSize1 = PageSize1.Letter,
    scale: Double = 3.0
) {
    val width = (pageSize.width*scale).roundToInt()
    val height = (pageSize.height*scale).roundToInt()
    val number = 0

    val scaledBitmap = webViewToScaledBitMap(webView, width, height)
    val attributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

    val pdfDoc = PrintedPdfDocument(context, attributes)
    val pageInfo = PdfDocument.PageInfo.Builder(width, height, number).create()
    val page = pdfDoc.startPage(pageInfo)
    try {
        page.canvas.drawBitmap(scaledBitmap, 0F, 0F, null)
        pdfDoc.finishPage(page)
    } catch (error: Exception) {
        error.printStackTrace()
    }
    val fileName = fileName(customer, transaction)
    val fileLocation = fileLocation(fileName)
    Log.d("Webview Loaded", "Successful")
    pdfDoc.finishPage(page)
    savePdfToFile(pdfDoc, fileLocation)
    Log.d("Print Function", "Successful")
    pdfDoc.close()
}