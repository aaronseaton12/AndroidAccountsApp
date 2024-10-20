package com.aaronseaton.accounts.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.TransactionSorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PaymentListState(
    val transactions: List<Payment> = listOf(Payment()),
    val customers: List<Customer> = listOf(Customer()),
    val sorting: TransactionSorting = TransactionSorting.BY_DATE,
    val accountUser: User = User(),
    val business: Business = Business(),
    val loading: Boolean = true
)


@HiltViewModel
class PaymentViewModels @Inject constructor(
    private val accountUser: User,
    private val businessRepo: EntityRepo<Business>,
    private val paymentRepo: EntityRepo<Payment>,
    private val customerRepo: EntityRepo<Customer>
): ViewModel() {
    private val _paymentState = MutableStateFlow(PaymentListState(loading = true))
    val paymentState: StateFlow<PaymentListState> = _paymentState.asStateFlow()

    init {
        updatePaymentState()
    }

    private fun updatePaymentState() = viewModelScope.launch {
        val accountUser = accountUser
        val business = async { businessRepo.get(accountUser.selectedBusiness)!!}
        combine(
            paymentRepo.liveList(),
            customerRepo.liveList()
        ) { payments, customers ->
            PaymentListState(
                transactions = payments,
                customers = customers,
                accountUser = accountUser,
                business = business.await()
            )
        }.collect { combinedFlow ->
            _paymentState.update {
                it.copy(
                    transactions = combinedFlow.transactions,
                    customers = combinedFlow.customers,
                    loading = false
                )
            }
        }
    }


    fun changeSorting(sorting: TransactionSorting) {
        viewModelScope.launch {
            _paymentState.update {
                it.copy(sorting = sorting)
            }
        }
    }

    fun updatePayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepo.update(payment.documentID, payment)
        }
    }

    fun deletePayment(transaction: FinancialTransaction) {
        viewModelScope.launch {
            paymentRepo.delete(transaction.documentID)
        }
    }

    fun insertPayment(payment: Payment) {
        viewModelScope.launch {
            paymentRepo.add(payment)
        }
    }
}