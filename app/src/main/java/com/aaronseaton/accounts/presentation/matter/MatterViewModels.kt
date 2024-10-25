package com.aaronseaton.accounts.presentation.matter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.repository.GenericQuery.WhereQuery
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.MatterSorting
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.GenericQuery
import com.aaronseaton.accounts.domain.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatterViewModels @Inject constructor(
    private val repoGroup: RepoGroup
): ViewModel() {
    
    private var matterRepo: EntityRepo<Matter>? = null
    private var matterID: String? = null
    fun setMatterId(matterID: String?) {this.matterID = matterID}

    private val sorting = MutableStateFlow(MatterSorting.BY_CUSTOMER_FIRSTNAME)
    fun changeSorting(matterSorting: MatterSorting) {
        viewModelScope.launch {
            sorting.update{matterSorting}
        }
    }

    val state = repoGroup.repos.map { repos ->
        matterRepo = repos.matter
        MatterListState(
            matterList = repos.matter.list(),
            loading = false
        ).also { Log.d(TAG, it.matterList.toString()) }
    }
    val individualState = repoGroup.repos.map { repos ->
        matterRepo = repos.matter
        coroutineScope {
            val matter = async { if(matterID==null) Matter() else repos.matter.get(matterID!!) }
            val customers = async { repos.customer.list() }
            val query = matterID?.let { WhereQuery("matter", it) }?: GenericQuery.NoQuery
            val receipts = async { repos.receipt.list(query) }
            val payments = async { repos.payment.list(query) }
            val tasks = async { repos.task.list(query) }
            val matters = async { repos.matter.list() }
            IndividualMatterState(
                accountUser = repos.accountUser,
                customers = customers.await(),
                customer = customers.await().find { it.documentID == matter.await().customerID }?: Customer(),
                matter = matter.await(),
                receipts = receipts.await(),
                payments = payments.await(),
                tasks = tasks.await(),
                matters = matters.await(),
                loading = false
            )
        }
    }

    fun insertMatter(matter: Matter) {
        viewModelScope.launch {
            matterRepo?.add(matter)
        }
    }

    companion object {
        const val TAG = "Matter List"
    }
}
data class MatterListState (
    val matterList: List<Matter> = emptyList(),
    val loading: Boolean = true,
)

data class IndividualMatterState(
    val accountUser: User = User(),
    val matter: Matter = Matter(),
    val customer: Customer = Customer(),
    val customers: List<Customer> = emptyList(),
    val loading: Boolean = true,
    val tasks: List<Task> = emptyList(),
    val receipts: List<Receipt> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val matters: List<Matter> = emptyList(),
)