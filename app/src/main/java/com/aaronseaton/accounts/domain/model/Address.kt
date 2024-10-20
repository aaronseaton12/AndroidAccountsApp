package com.aaronseaton.accounts.domain.model

data class Address(
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val country: String = ""
){
    override fun toString(): String =
        when {
            addressLine1.addComma() + addressLine2.addComma() + city == "" -> country
            else -> addressLine1.addComma() + addressLine2.addComma() + city
        }
    private fun String.addComma(): String{
        return if(this.isNotBlank()) {
            "$this, "
        } else ""
    }
}
