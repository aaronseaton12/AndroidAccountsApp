package com.aaronseaton.accounts.presentation.receipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class AddReceiptViewModel @Inject constructor(
    private val customerRepo: EntityRepo<Customer>,
    private val receiptRepo: EntityRepo<Receipt>,
) : ViewModel() {


    private val _list = MutableStateFlow(ReceiptListState(loading = true))
    val list: StateFlow<ReceiptListState> = _list.asStateFlow()

    init {
        updateReceiptState()
    }

    fun updateReceiptState() {
        viewModelScope.launch {
            customerRepo.liveList().collect { customers ->
                _list.update {
                    it.copy(
                        customers = customers,
                        loading = false
                    )
                }
            }
        }
    }

    fun insertReceipt(receipt: Receipt) {
        viewModelScope.launch {
            receiptRepo.add(receipt)
        }
    }
}