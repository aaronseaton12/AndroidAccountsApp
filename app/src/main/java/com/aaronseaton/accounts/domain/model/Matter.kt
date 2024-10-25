package com.aaronseaton.accounts.domain.model

import com.google.firebase.firestore.DocumentId
import java.util.Calendar
import java.util.Date

data class Matter (
    @DocumentId
    override val documentID: String = "",
    val customerID: String  = "",
    val createdAt: Date = Calendar.getInstance().time,
    val description: String  = "",
    val title: String  = "",
    val type:String = MatterType.CIVIL.type,
    val responsibleAttorney:String  = "",
    val createdBy: String  = "",
    val otherAttorneys: List<String>?  = null,
    val number:Double  = 0.0,
    val open: Boolean? = true,
): FirebaseEntity {}


enum class MatterType(val type: String) {
    CIVIL ("Civil"),
    CRIMINAL ("Criminal"),
    CONSTITUTIONAL ("Constitutional"),
    POWEROFATTORNEY ("Power of Attorney"),
    CONVEYANCING ("Conveyancing"),
    PROBATE ("Probate"),
    EMPLOYMENT ("Employment"),
    WILL ("Will"),

}