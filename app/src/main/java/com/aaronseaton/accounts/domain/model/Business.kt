package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId

data class Business(
    val businessID: String = "",
    val name: String = "",
    val emailAddress: String = "",
    val address: Address = Address(),
    val phoneNumber: PhoneNumber = PhoneNumber(),
    val logo: String = "",
    val members: MutableList<String> = mutableListOf(),
    val pendingMembers: MutableList<String> = mutableListOf(),
    val owner: String = "",
    @DocumentId
    override val documentID: String = ""
) : FirebaseEntity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Business

        if (businessID != other.businessID) return false
        if (name != other.name) return false
        if (emailAddress != other.emailAddress) return false
        if (address != other.address) return false
        if (phoneNumber != other.phoneNumber) return false
        if (logo != other.logo) return false
        if (owner != other.owner) return false
        if (documentID != other.documentID) return false

        return true
    }

    override fun hashCode(): Int {
        var result = businessID.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + emailAddress.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + logo.hashCode()
        result = 31 * result + owner.hashCode()
        result = 31 * result + documentID.hashCode()
        return result
    }

    fun trimAllFields(): Business {
        return this.copy(
            name = name.trim(),
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
