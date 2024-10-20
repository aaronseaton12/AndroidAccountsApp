package com.aaronseaton.accounts.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.CustomerSorting
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.util.SearchWidgetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerListState(
    val customers: List<Customer> = listOf(Customer()),
    val query: String = "",
    val searchState: SearchWidgetState = SearchWidgetState.CLOSED,
    val sorting: CustomerSorting = CustomerSorting.BY_FIRST_NAME,
    val loading: Boolean = true
)

private const val TAG = "Customer ViewModel"

@HiltViewModel
class CustomerViewModels @Inject constructor(
    private val customerRepo: EntityRepo<Customer>
) : ViewModel() {
    init {
        println("Check Time: Activity Init in $TAG")
    }
    private val _customerListState = MutableStateFlow(CustomerListState(loading = true))
    val customerListState: StateFlow<CustomerListState> = _customerListState.asStateFlow()

    init {
        refreshCustomers()
    }

    private fun refreshCustomers() {
        viewModelScope.launch {
            customerRepo.liveList().collect { customers ->
                _customerListState.update {
                    it.copy(
                        customers = customers,
                        loading = false
                    )
                }
            }
        }
    }

    /**
     * To search for a specific customer
     * @param query The string to search
     */
    fun onQueryChange(query: String) {
        println("On Query Changed Clicked")
        _customerListState.update { list ->
            list.copy(
                query = query,
                loading = false
            )
        }
    }

    fun onCloseClicked() {
        println("On Close Clicked")
        _customerListState.update { list ->
            list.copy(
                searchState = SearchWidgetState.CLOSED,
                loading = false
            )
        }
    }

    fun onOpenClicked() {
        println("On Open Clicked")
        _customerListState.update { list ->
            list.copy(
                searchState = SearchWidgetState.OPENED,
                loading = false
            )
        }
    }

    fun changeSorting(sorting: CustomerSorting) {
        println("On Change Sorting Clicked")
        _customerListState.update { list ->
            list.copy(
                sorting = sorting,
                loading = false
            )
        }
    }
}
