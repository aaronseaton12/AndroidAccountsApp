package com.aaronseaton.accounts.presentation.payment

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.TransactionCard


@Composable
fun ListOfPayments(
    navigateTo: (String) -> Unit = {},
    viewModel: PaymentViewModels = hiltViewModel()
) {
    //viewModel.updatePaymentState()
    val state by viewModel.state.collectAsState(initial = PaymentListState())

    ListOfPaymentImpl(
        navigateTo,
        viewModel::changeSorting,
        state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfPaymentImpl(
    navigateTo: (String) -> Unit,
    changeSorting: (Sorting.TransactionSorting) -> Unit,
    state: PaymentListState,
) {
    val icon = Icons.Default.ArrowBack
    val onHomeButtonClicked = { navigateTo(Routes.HOME) }
    val onFabPressed = { navigateTo(Routes.ADD_PAYMENT + "/ ") }
    val title = "List of Expenses"
    val description = "Back"
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = onHomeButtonClicked) {
                            Icon(icon, description)
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Search"
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Sort By Date") },
                                onClick = { changeSorting(Sorting.TransactionSorting.BY_DATE) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Customer") },
                                onClick = { changeSorting(Sorting.TransactionSorting.BY_CUSTOMER_FIRSTNAME) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Value") },
                                onClick = { changeSorting(Sorting.TransactionSorting.BY_VALUE) }
                            )
                        }
                    }
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.PAYMENT_LIST) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Expense") },
                icon = { Icon(Icons.Default.Add, "Add Expense") },
                onClick = onFabPressed
            )
        }
    ) { paddingValues ->
        when (state.loading) {
            true -> LoadingScreen()
            false -> OutputAreaPayments(
                navigateTo,
                state.sorting,
                state.transactions,
                state.customers,
                state.accountUser,
                state.business,
                Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun OutputAreaPayments(
    navigateTo: (String) -> Unit,
    sorting: Sorting.TransactionSorting,
    payments: List<Payment>,
    customers: List<Customer>,
    accountUser: User,
    business: Business,
    modifier: Modifier
) {
    val filteredPayments = when (sorting) {
        Sorting.TransactionSorting.BY_DATE -> payments.sortedBy { it.date }.reversed()
        Sorting.TransactionSorting.BY_CUSTOMER_FIRSTNAME -> payments.sortedBy { payment ->
            payment.customerID.let { id ->
                customers.single { it.documentID == id }
            }.firstName
        }
        Sorting.TransactionSorting.BY_VALUE -> payments.sortedBy { it.amount }.reversed()
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 150.dp), modifier = modifier) {
        items(filteredPayments) { payment ->

            TransactionCard(
                transaction = payment,
                customer = customers.single { it.documentID == payment.customerID },
                accountUser = accountUser,
                business = business,
                navigateTo = navigateTo,
            )
        }
    }
}
//
//@Preview
//@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Preview("Large Screen", device = Devices.PIXEL_C)
//@Composable
//fun TestPaymentList() {
//    AccountsTheme {
//        ListOfPaymentImpl(
//            navigateTo = {},
//            payments = TestInfo.listOfTestPayments,
//            customers = TestInfo.listOfCustomers,
//            sorting = PaymentSorting.BY_VALUE
//        ) {}
//    }
//
//}