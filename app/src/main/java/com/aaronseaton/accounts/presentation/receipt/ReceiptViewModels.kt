package com.aaronseaton.accounts.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Receipt
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

data class ReceiptListState(
    val receipts: List<Receipt> = listOf(Receipt()),
    val customers: List<Customer> = listOf(Customer()),
    val sorting: TransactionSorting = TransactionSorting.BY_DATE,
    val accountUser: User = User(),
    val business: Business = Business(),
    val loading: Boolean = true
)

@HiltViewModel
class ReceiptViewModels @Inject constructor(
    private val accountUser: User,
    private val businessRepo: EntityRepo<Business>,
    private val receiptRepo: EntityRepo<Receipt>,
    private val customerRepo: EntityRepo<Customer>
): ViewModel() {

    private val _list = MutableStateFlow(ReceiptListState(loading = true))
    val list: StateFlow<ReceiptListState> = _list.asStateFlow()

    init {
        updateReceiptState()
    }

    private fun updateReceiptState() = viewModelScope.launch {
        val accountUser = accountUser
        val business = async { businessRepo.get(accountUser.selectedBusiness) }
        combine(
            receiptRepo.liveList(),
            customerRepo.liveList()
        ) { receipts, customers ->
            ReceiptListState(
                receipts,
                customers
            )
        }.collect { combinedFlow ->
            _list.update {
                it.copy(
                    receipts = combinedFlow.receipts,
                    customers = combinedFlow.customers,
                    accountUser = accountUser,
                    business = business.await(),
                    loading = false
                )
            }
        }
    }

    fun changeSorting(sorting: TransactionSorting) {
        viewModelScope.launch {
            _list.update {
                it.copy(sorting = sorting)
            }
        }
    }
}
