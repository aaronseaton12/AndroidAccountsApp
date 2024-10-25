package com.aaronseaton.accounts.presentation.receipt

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ReceiptViewModels @Inject constructor(
    private val repoGroup: RepoGroup
) : ViewModel() {

    private val sorting = MutableStateFlow(Sorting.TransactionSorting.BY_DATE)
    private val loading = MutableStateFlow(false)
    private var receiptRepo: EntityRepo<Receipt>? = null
    private var receiptID: String? = null
    fun setReceiptId(receiptID: String?) {this.receiptID = receiptID}
    private var customerID: String? = null
    fun setCustomerId(customerID: String?) {this.customerID = customerID}

    val state = repoGroup.repos.map { repos ->
        coroutineScope {
            receiptRepo = repos.receipt
            val business = async { repos.business.get(repos.accountUser.selectedBusiness) }
            val customers = async { repos.customer.list() }
            val receipts = async { repos.receipt.list() }
            val accountUser = repos.accountUser
            ReceiptListState(
                receipts = receipts.await(),
                customers = customers.await(),
                accountUser = accountUser,
                business = business.await(),
                loading = false
            )
        }
    }.combine(sorting) { paymentList, sorting ->
        paymentList.copy(sorting = sorting)
    }


    val individualState = repoGroup.repos.map { repos ->
        receiptRepo = repos.receipt
        coroutineScope {
            val accountUser = repos.accountUser
            val customers = async { repos.customer.list() }
            val business = async { repos.business.get(repos.accountUser.selectedBusiness) }
            if (receiptID.isNullOrBlank()) {
                val customer = async {
                    if (customerID.isNullOrBlank()) Customer()
                    else  repos.customer.get(customerID!!)
                }
                val matter = Matter()
                ReceiptIndividualState(
                    customer = customer.await(),
                    transaction = Receipt(),
                    business = business.await(),
                    accountUser = accountUser,
                    matter = matter,
                    loading = false,
                    customers = customers.await()
                ).also { Log.d(TAG, "Sending Blank State") }
            }
            else {
                val receipt = async { repos.receipt.get(receiptID!!) }
                val customer = async { repos.customer.get(receipt.await().customerID) }
                val matter = async {
                    if (receipt.await().matter.isNullOrBlank()) Matter()
                    else  repos.matter.get(receipt.await().matter!!)
                }
                ReceiptIndividualState(
                    transaction = receipt.await(),
                    customer = customer.await(),
                    accountUser = accountUser,
                    business = business.await(),
                    customers = customers.await(),
                    matter = matter.await(),
                    loading = false
                ).also { Log.d(TAG, "Sending NOT blank ") }
            }
        }
    }.combine(loading){ individualState, loading ->
        individualState.copy(
            loading = loading
        )
    }
    fun setLoadingTrue(){
        loading.update { true }
    }
    fun setLoadingFalse(){
        loading.update { false }
    }


    fun changeSorting(newValue: Sorting.TransactionSorting) {
        sorting.update { newValue }
    }

    fun insertReceipt(receipt: Receipt) {
        viewModelScope.launch {
            receiptRepo?.add(receipt)
        }
    }

    fun updateReceipt(receipt: Receipt) {
        viewModelScope.launch {
            receiptRepo?.update(receipt.documentID, receipt)
        }
    }

    fun deleteReceipt(transaction: FinancialTransaction) {
        viewModelScope.launch {
            receiptRepo?.delete(transaction.documentID)
        }
    }
}

data class ReceiptListState(
    val receipts: List<Receipt> = listOf(Receipt()),
    val customers: List<Customer> = listOf(Customer()),
    val accountUser: User = User(),
    val business: Business = Business(),
    val sorting: Sorting.TransactionSorting = Sorting.TransactionSorting.BY_DATE,
    val loading: Boolean = true
)

data class ReceiptIndividualState(
    val transaction: Receipt = Receipt(),
    val customer: Customer = Customer(),
    val matter: Matter = Matter(),
    val accountUser: User = User(),
    val business: Business = Business(),
    val customers: List<Customer> = listOf(Customer()),
    val loading: Boolean = true
)