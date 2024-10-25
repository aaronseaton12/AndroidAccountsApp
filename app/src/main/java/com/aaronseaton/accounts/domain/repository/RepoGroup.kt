package com.aaronseaton.accounts.domain.repository

import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import kotlinx.coroutines.flow.Flow

interface RepoGroup {
    val repos: Flow<Repos>
    data class Repos(
        val accountUser: User,
        val user: EntityRepo<User>,
        val business: EntityRepo<Business>,
        val customer: EntityRepo<Customer>,
        val task: EntityRepo<Task>,
        val receipt: EntityRepo<Receipt>,
        val payment: EntityRepo<Payment>,
        val matter: EntityRepo<Matter>,
    )

}

