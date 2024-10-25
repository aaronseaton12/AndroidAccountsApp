package com.aaronseaton.accounts.util

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class Util {
    companion object {
        val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
        val prettyDateFormatter = SimpleDateFormat("EEE, MMM dd, yyyy @ h:mm a", Locale.US)
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)
        val decimalFormat = DecimalFormat("#,###,##0.00")
        val paymentFormat = DecimalFormat("0000")
        fun String.isValidDoubleString(): Boolean {
            if (this.isEmpty()) {
                println("String cannot be parsed, it is null or empty.")
                return false
            }
            try {
                this.toDouble()
                return true
            } catch (e: NumberFormatException) {
                println("Input String cannot be parsed to Integer.")
            }
            return false
        }

        fun String.isValidDoubleString(formatter: DecimalFormat): Boolean {
            if (this == "") {
                println("String cannot be parsed, it is null or empty.")
                return false
            }
            try {
                formatter.parse(this)
                return true
            } catch (e: NumberFormatException) {
                println("Input String cannot be parsed to Integer.")
            }
            return false
        }

        fun Double.format(): String = decimalFormat.format(this)
    }
}