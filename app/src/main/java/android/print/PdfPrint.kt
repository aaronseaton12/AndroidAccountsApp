package android.print

import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.util.Log
import java.io.File

class PdfPrint(private val printAttributes: PrintAttributes) {
    fun print(printAdapter: PrintDocumentAdapter, file: File) {
        printAdapter.onLayout(null, printAttributes, null, object : LayoutResultCallback() {
            override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                printAdapter.onWrite(
                    arrayOf(PageRange.ALL_PAGES),
                    getOutputFile(file),
                    CancellationSignal(),
                    object : WriteResultCallback() {
                        override fun onWriteFinished(pages: Array<PageRange>) {
                            super.onWriteFinished(pages)
                        }
                    }
                )
            }
        }, null)
    }

    private fun getOutputFile(file: File): ParcelFileDescriptor? {
        try {
            file.createNewFile()
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open ParcelFileDescriptor", e)
        }
        return null
    }

    companion object {
        private val TAG = PdfPrint::class.java.simpleName
    }
}

