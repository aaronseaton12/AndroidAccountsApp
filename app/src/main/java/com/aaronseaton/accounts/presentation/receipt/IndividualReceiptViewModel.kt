package com.aaronseaton.accounts.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Receipt
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

@HiltViewModel

class IndividualReceiptViewModel @Inject constructor(
	private val accountUser: User,
	private val businessRepo: EntityRepo<Business>,
	private val receiptRepo: EntityRepo<Receipt>,
	private val customerRepo: EntityRepo<Customer>
) : ViewModel() {
	private val _individual = MutableStateFlow(ReceiptIndividualState(loading = true))
	val individual: StateFlow<ReceiptIndividualState> = _individual.asStateFlow()

	fun updateIndividualReceiptState(receiptID: String) {
		viewModelScope.launch {
			val receipt = async { receiptRepo.get(receiptID) }
			val customer = async { customerRepo.get(receipt.await().customerID) }
			val accountUser = accountUser
			val business = async { businessRepo.get(accountUser.selectedBusiness) }
			customerRepo.liveList().collect { customers ->
				_individual.update {
					it.copy(
						transaction = receipt.await(),
						accountUser = accountUser,
						business = business.await(),
						customer = customer.await(),
						customers = customers,
						loading = false
					)
				}
			}
		}
	}

	fun updateReceipt(receipt: Receipt) {
		viewModelScope.launch {
			receiptRepo.update(receipt.documentID, receipt)
		}
	}

	fun deleteReceipt(receipt: FinancialTransaction) {
		viewModelScope.launch {
			receiptRepo.delete(receipt.documentID)
		}
	}

	fun insertReceipt(receipt: Receipt) {
		viewModelScope.launch {
			receiptRepo.add(receipt)
		}
	}
}

data class ReceiptIndividualState(
    val transaction: Receipt = Receipt(),
    val accountUser: User = User(),
    val business: Business = Business(),
    val customer: Customer = Customer(),
    val customers: List<Customer> = listOf(Customer()),
    val loading: Boolean = true
)
