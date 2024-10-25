package com.aaronseaton.accounts.domain.model

enum class PaymentMethod(val type: String) {
    CASH("Cash"),
    BANK("Bank"),
    CHEQUE("Cheque"),
    CARD("Card"),
    OTHER("Other");

    override fun toString(): String {
        return this.type
    }
}