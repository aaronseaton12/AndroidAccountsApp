package com.aaronseaton.accounts.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import com.aaronseaton.accounts.presentation.receipt.TAG
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
class PaymentViewModels @Inject constructor(
    private val repoGroup: RepoGroup,
): ViewModel() {
    private val sorting = MutableStateFlow(Sorting.TransactionSorting.BY_DATE)
    private val loading = MutableStateFlow(false)
    private var paymentRepo: EntityRepo<Payment>? = null
    private var paymentID: String? = null
    fun setPaymentId(paymentID: String?) {this.paymentID = paymentID}
    private var customerID: String? = null
    fun setCustomerId(customerID: String?) {this.customerID = customerID}

    val state = repoGroup.repos.map{ repos ->
        coroutineScope {
            paymentRepo = repos.payment
            val business = async{ repos.business.get(repos.accountUser.selectedBusiness) }
            val transactions = async{ repos.payment.list() }
            val customers = async{ repos.customer.list()  }
            PaymentListState(
                transactions = transactions.await(),
                customers = customers.await(),
                accountUser = repos.accountUser,
                business = business.await(),
                loading = false
            )
        }
    }.combine(sorting){paymentList, sorting ->
        paymentList.copy(sorting = sorting)
    }
    val individualState = repoGroup.repos.map{ repos ->
        paymentRepo = repos.payment
        coroutineScope {
            val accountUser = repos.accountUser
            val customers = async { repos.customer.list() }
            val business = async { repos.business.get(repos.accountUser.selectedBusiness) }
            if (paymentID.isNullOrBlank()) {
                val customer = async {
                    if (customerID.isNullOrBlank()) Customer()
                    else  repos.customer.get(customerID!!)
                }
                PaymentIndividualState(
                    customer = customer.await(),
                    matter = Matter(),
                    transaction = Payment(),
                    business = business.await(),
                    accountUser = accountUser,
                    loading = false,
                    customers = customers.await()
                ).also { Log.d(TAG, "Sending Blank State") }
            }
            else {
                val payment = async { repos.payment.get(paymentID!!) }
                val customer = async { repos.customer.get(payment.await().customerID) }
                val matter = async {
                    if (payment.await().matter.isNullOrBlank()) Matter()
                    else  repos.matter.get(payment.await().matter!!)
                }
                PaymentIndividualState(
                    transaction = payment.await(),
                    customer = customer.await(),
                    accountUser = accountUser,
                    business = business.await(),
                    customers = customers.await(),
                    matter = matter.await(),
                    loading = false
                ).also { Log.d(TAG, "Sending NOT blank ") }
            }
        }
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

    fun insertPayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepo?.add( payment)
        }
    }
    fun updatePayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepo?.update(payment.documentID, payment)
        }
    }

    fun deletePayment(transaction: FinancialTransaction) {
        viewModelScope.launch {
            paymentRepo?.delete(transaction.documentID)
        }
    }
}

data class PaymentListState(
    val transactions: List<Payment> = listOf(Payment()),
    val customers: List<Customer> = listOf(Customer()),
    val sorting: Sorting.TransactionSorting = Sorting.TransactionSorting.BY_DATE,
    val accountUser: User = User(),
    val business: Business = Business(),
    val loading: Boolean = true
)

data class PaymentIndividualState(
    val transaction: Payment = Payment(),
    val customer: Customer = Customer(),
    val matter: Matter = Matter(),
    val accountUser: User = User(),
    val business: Business = Business(),
    val customers: List<Customer> = listOf(Customer()),
    val loading: Boolean = true
)