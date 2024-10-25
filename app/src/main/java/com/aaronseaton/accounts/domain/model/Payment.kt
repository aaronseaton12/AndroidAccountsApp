package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.*


data class Payment(

    override val id: String = "",
    override val date: Date = Calendar.getInstance().time,
    override val amount: Double = 0.0,
    override val customerID: String = "",
    @DocumentId
    override val documentID: String = "",
    override val reason: String? = null,
    override val payMethod: String? = null,
    override val matter: String? = null
) : FinancialTransaction, FirebaseEntity {
    private fun String.newLine(string: String): String {
        return this + "\n" + string
    }

    override fun transactionName(): String {
        return "Payment"
    }

    override fun toString(): String {
        return id
            .newLine(amount.toString())
            .newLine(date.toString())
            .newLine(customerID.toString())
    }
}
