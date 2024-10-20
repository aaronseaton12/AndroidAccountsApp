package com.aaronseaton.accounts.domain.model

interface FirebaseEntity {
    val documentID: String
}

object NullFirebaseObject : FirebaseEntity {
    override val documentID: String = ""
}