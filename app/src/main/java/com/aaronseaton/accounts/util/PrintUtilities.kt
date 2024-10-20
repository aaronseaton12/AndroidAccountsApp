package com.aaronseaton.accounts.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.print.PrintHelper
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

fun webViewPrint(
    context: Context,
    htmlString: String,
    fileName: String,
    pageSize: PageSize = PageSize.Letter,
    scale:Double = 1.0
) {
    val webView = WebView(context)
    val width = (pageSize.width*scale).roundToInt()
    val height = (443.2).roundToInt()
    webView.layout(0, 0, width, height)
    webView.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
        override fun onPageFinished(view: WebView, url: String) {
            Log.i(TAG, "page finished loading $url")
            createWebPrintJob(view, context, fileName)
        }
    }
    webView.loadDataWithBaseURL(null, htmlString, "text/HTML", "UTF-8", null)
}
private fun alternatePrint(view: WebView, context: Context, width:Int, height:Int) {
    PrintHelper(context).apply {
        scaleMode = PrintHelper.SCALE_MODE_FIT
    }.also { printHelper ->
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width , height , true)
        printHelper.printBitmap("droids.jpg - test print", scaledBitmap)
    }

}
private fun createWebPrintJob(
    webView: WebView,
    context: Context,
    jobName: String = "Accounts App Document"
) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager

    printManager.let { manager ->
        val printAdapter = webView.createPrintDocumentAdapter(jobName)
        manager.print(
            jobName,
            printAdapter,
            PrintAttributes.Builder().build()
        ).also { printJob ->
            var printJobs = printJob
        }
    }
}

