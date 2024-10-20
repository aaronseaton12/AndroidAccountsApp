package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId

data class Customer(
    val customerID: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val emailAddress: String = "",
    val address: Address = Address(),
    val phoneNumber: PhoneNumber = PhoneNumber(),
    @DocumentId
    override val documentID: String = ""

) : FirebaseEntity {
    fun fullName(): String {
        return "$firstName $middleName $lastName"
            .replace("  ", " ")
            .trim()
    }

    private fun String.newLine(string: String): String {
        return this + "\n" + string
    }

    override fun toString(): String {
        return fullName()
    }

    fun trimAllFields(): Customer {
        return this.copy(
            firstName = firstName.trim(),
            middleName = middleName.trim(),
            lastName = lastName.trim(),
            emailAddress = emailAddress.trim(),
            address = Address(
                address.addressLine1.trim(),
                address.addressLine2.trim(),
                address.city.trim(),
                address.country.trim()
            ),
            phoneNumber = PhoneNumber(
                phoneNumber.cellNumber.trim(),
                phoneNumber.homeNumber.trim(),
                phoneNumber.workNumber.trim()
            )
        )
    }
}
