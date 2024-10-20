package com.aaronseaton.accounts.domain.model

data class PhoneNumber(
    val cellNumber: String = "",
    val homeNumber: String = "",
    val workNumber: String = ""
) {
    override fun toString(): String {
        return "$cellNumber " +
                "${homeNumber.addDivider()} " +
                workNumber.addDivider()
    }
}

fun String.addDivider(): String {
    return if (this.isNotBlank()) {
        "| $this"
    } else ""
}
