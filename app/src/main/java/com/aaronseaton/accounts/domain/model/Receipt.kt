package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.*

data class Receipt(
    override val paymentID: String = "",
    override val date: Date = Calendar.getInstance().time,
    override val amount: Double = 0.0,
    override val customerID: String = "",
    @DocumentId
    override val documentID: String = "",
    override val reason: String? = null,
    override val payMethod: String? = null
) : FinancialTransaction, FirebaseEntity {
    private fun String.newLine(string: String): String {
        return this + "\n" + string
    }

    override fun transactionName(): String {
        return "Receipt"
    }

    override fun toString(): String {
        return paymentID
            .newLine(amount.toString())
            .newLine(date.toString())
            .newLine(customerID)
    }
}
