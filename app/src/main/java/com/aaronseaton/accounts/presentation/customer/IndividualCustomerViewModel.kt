package com.aaronseaton.accounts.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IndividualCustomerState(
    val customer: Customer = Customer(),
    val transactions: List<FinancialTransaction> = listOf(Receipt()),
    val loading: Boolean = true
)

private const val TAG = "Customer ViewModel"

@HiltViewModel
class IndividualCustomerViewModel @Inject constructor(
    private val paymentRepo: EntityRepo<Payment>,
    private val receiptRepo: EntityRepo<Receipt>,
    private val customerRepo: EntityRepo<Customer>
) : ViewModel() {
    init {
        println("Check Time: Activity Init in $TAG")
    }

    private val _individualCustomerState = MutableStateFlow(IndividualCustomerState(loading = true))
    val individualCustomerState: StateFlow<IndividualCustomerState> =
        _individualCustomerState.asStateFlow()

    fun updateIndividualCustomerState (customerID: String){
        viewModelScope.launch {
            val customer = customerRepo.get(customerID)
            combine(
                paymentRepo.liveList().map { 
                    payments -> payments.filter { it.customerID == customerID } },
                receiptRepo.liveList().map { 
                    receipts -> receipts.filter { it.customerID == customerID } }
            ){ payments, receipts ->
                IndividualCustomerState(
                    customer = customer,
                    transactions = payments + receipts,
                    loading = false
                )
            }.collect{ collectedState ->
                _individualCustomerState.update {
                    it.copy(
                        customer = collectedState.customer,
                        transactions = collectedState.transactions,
                        loading = collectedState.loading
                    )
                }
            }
        }
    }

    fun getCustomer(customerID: String) {
        viewModelScope.launch {
            val customer = customerRepo.get(customerID)
            _individualCustomerState.update {
                it.copy(
                    customer = customer,
                    loading = false
                )
            }
        }
    }

    fun insertCustomer(customer: Customer) {
        viewModelScope.launch{
            customerRepo.add(customer)
        }
    }

    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            customerRepo.update(customer.documentID, customer)
        }
    }
}


