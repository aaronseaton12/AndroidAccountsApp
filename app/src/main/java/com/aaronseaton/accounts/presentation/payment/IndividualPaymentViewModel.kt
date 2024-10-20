package com.aaronseaton.accounts.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PaymentIndividualState(
    val payment: Payment = Payment(),
    val customer: Customer = Customer(),
    val accountUser: User = User(),
    val business: Business = Business(),
    val customers: List<Customer> = listOf(Customer()),
    val loading: Boolean = true
)

@HiltViewModel
class IndividualPaymentViewModel @Inject constructor(
    private val accountUser: User,
    private val paymentRepo: EntityRepo<Payment>,
    private val businessRepo: EntityRepo<Business>,
    private val customerRepo: EntityRepo<Customer>
) : ViewModel() {
    private val _paymentIndividualState = MutableStateFlow(PaymentIndividualState(loading = true))
    val paymentIndividualState: StateFlow<PaymentIndividualState> =
        _paymentIndividualState.asStateFlow()

    fun updateIndividualPaymentState(paymentID: String) {
        viewModelScope.launch {
            val accountUser = accountUser
            val business = async { businessRepo.get(accountUser.selectedBusiness) }
            val payment = async { paymentRepo.get(paymentID) }
            val customer = async { customerRepo.get(payment.await().customerID) }
            customerRepo.liveList().collect { customers ->
                _paymentIndividualState.update {
                    it.copy(
                        payment = payment.await(),
                        customer = customer.await(),
                        accountUser = accountUser,
                        business = business.await(),
                        customers = customers,
                        loading = false
                    )
                }
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