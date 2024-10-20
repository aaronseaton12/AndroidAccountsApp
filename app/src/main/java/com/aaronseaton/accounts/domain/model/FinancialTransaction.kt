package com.aaronseaton.accounts.domain.model

import java.util.*

interface FinancialTransaction {
    val paymentID: String
    val date: Date
    val amount: Double
    val customerID: String
    val documentID: String
    val reason: String?
    val payMethod: String?
    fun transactionName(): String
}

