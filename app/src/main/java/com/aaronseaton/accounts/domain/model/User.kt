package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
typealias Token = String

@IgnoreExtraProperties
data class User(

    @DocumentId
    override val documentID: String = "",
    val userID: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val lastName: String = "",
    val emailAddress: String = "",
    val address: Address = Address(),
    val phoneNumber: PhoneNumber = PhoneNumber(),
    val selectedBusiness: String = "",
    val photoUrl: String? = null,
    val signature: String? = null,
    val notificationTokens: MutableList<String> = mutableListOf()
) : FirebaseEntity {
    val fullName: String
        get() = "$firstName $middleName $lastName"
            .replace("  ", " ")
            .trim()

    fun fullName(): String {
        return "$firstName $middleName $lastName"
            .replace("  ", " ")
            .trim()
    }

    fun trimAllFields(): User {
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