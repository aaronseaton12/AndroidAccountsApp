package com.aaronseaton.accounts.presentation.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.GenericQuery
import com.aaronseaton.accounts.domain.repository.RepoGroup
import com.aaronseaton.accounts.util.SearchWidgetState
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
class CustomerViewModels @Inject constructor(
    private val repoGroup: RepoGroup
) : ViewModel() {
    private var customerRepo: EntityRepo<Customer>? = null
    private val search = MutableStateFlow(SearchWidgetState.CLOSED)
    private val query = MutableStateFlow("")
    private val sort = MutableStateFlow(Sorting.CustomerSorting.BY_FIRST_NAME)
    val searchBar = combine(
        search,
        query,
        sort
    ){ search, query, sorting->
        CustomerListState(
            query = query,
            searchState = search,
            sorting = sorting,
            loading = false
        )
    }

    val customerListState = repoGroup.repos.map { repos ->
        coroutineScope {
            val customers = async { repos.customer.list() }
            CustomerListState(
                customers = customers.await(),
                loading = false
            )
        }
    }

    fun onQueryChange(q: String){
        query.update { q }
    }

    fun onCloseClicked() {
        search.update { SearchWidgetState.CLOSED }
    }

    fun onOpenClicked() {
        search.update { SearchWidgetState.OPENED }
    }

    fun changeSorting (sorting: Sorting.CustomerSorting) {
        sort.update { sorting }
    }
    fun individualCustomerState(id: String? = null) = repoGroup.repos.map { repos ->
        coroutineScope {
            customerRepo = repos.customer
            if (id == null) {
                IndividualCustomerState()
            } else {
                val payments = async { repos.payment.list(GenericQuery.WhereQuery("customerID", id))}
                val receipts = async { repos.receipt.list(GenericQuery.WhereQuery("customerID", id))}
                val tasks = async { repos.task.list(GenericQuery.WhereQuery("customerID", id)) }
                val matters = async { repos.matter.list(GenericQuery.WhereQuery("customerID", id)) }
                val customer = async { repos.customer.get(id)}
                val accountUser = repos.accountUser
                IndividualCustomerState(
                    customer = customer.await(),
                    accountUser = accountUser,
                    tasks = tasks.await(),
                    matters = matters.await(),
                    transactions = payments.await() + receipts.await(),
                    loading = false
                )
            }
        }
    }
    fun insertCustomer(customer: Customer) {
        viewModelScope.launch {
            customerRepo?.add(customer)
        }
    }
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            customerRepo?.update(customer.documentID, customer)
        }
    }
    companion object{
        private const val TAG = "Customer ViewModel"
    }
}

data class CustomerListState(
    val customers: List<Customer> = listOf(Customer()),
    val query: String = "",
    val searchState: SearchWidgetState = SearchWidgetState.CLOSED,
    val sorting: Sorting.CustomerSorting = Sorting.CustomerSorting.BY_FIRST_NAME,
    val loading: Boolean = true
)
data class IndividualCustomerState(
    val customer: Customer = Customer(),
    val transactions: List<FinancialTransaction> = listOf(Receipt()),
    val accountUser: User = User(),
    val tasks: List<Task> = listOf(Task()),
    val matters: List<Matter> = listOf(Matter()),
    val loading: Boolean = true
)