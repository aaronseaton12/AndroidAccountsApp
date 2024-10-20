package com.aaronseaton.accounts.presentation.receipt

import android.content.res.Configuration
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TransactionSorting
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.TransactionCardTwo

@Composable
fun ListOfReceipts(
    navigateTo: (String) -> Unit = {},
    viewModel: ReceiptViewModels = hiltViewModel()
) {
    val state by viewModel.list.collectAsState()
    ListOfReceiptImpl(
        navigateTo,
        viewModel::changeSorting,
        state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfReceiptImpl(
    navigateTo: (String) -> Unit,
    changeSorting: (TransactionSorting) -> Unit,
    state: ReceiptListState
) {
    val icon = Icons.Default.ArrowBack
    val onPressed = { navigateTo(Routes.HOME) }
    val onFabPressed = { navigateTo(Routes.ADD_RECEIPT + "/ ") }
    val title = "List of Income Received"
    val description = "Back"
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = title)
                    },
                    navigationIcon = {
                        IconButton(onClick = onPressed) {
                            Icon(icon, description)
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort By Date") },
                                onClick = { changeSorting(TransactionSorting.BY_DATE) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Customer") },
                                onClick = { changeSorting(TransactionSorting.BY_CUSTOMER_FIRSTNAME) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Value") },
                                onClick = { changeSorting(TransactionSorting.BY_VALUE) }
                            )
                        }

                    }
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.RECEIPT_LIST) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Income") },
                icon = { Icon(Icons.Default.Add, "Add Income") },
                onClick = onFabPressed
            )
        }
    ) { padding ->
        when (state.loading) {
            true -> LoadingScreen()
            false -> OutputAreaPayments(
                state.receipts,
                state.customers,
                state.sorting,
                state.accountUser,
                state.business,
                navigateTo,
                Modifier.padding(padding)
            )
        }
    }
}

@Composable
fun OutputAreaPayments(
    receipts: List<Receipt>,
    customers: List<Customer>,
    sorting: TransactionSorting,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier
) {
    val sortedReceipts = when (sorting) {
        TransactionSorting.BY_DATE -> receipts.sortedBy { it.date }.reversed()
        TransactionSorting.BY_CUSTOMER_FIRSTNAME -> receipts.sortedBy { receipt ->
            receipt.customerID.let { id ->
                customers.single { it.documentID == id }
            }.firstName
        }
        TransactionSorting.BY_VALUE -> receipts.sortedBy { it.amount }.reversed()
    }


    LazyColumn(contentPadding = PaddingValues(bottom = 150.dp), modifier = modifier) {
        items(sortedReceipts) { receipt ->
            TransactionCardTwo(
                transaction = receipt,
                customer = customers.single { it.documentID == receipt.customerID },
                accountUser = accountUser,
                business = business,
                navigateTo = navigateTo
            )
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Home screen (large screen)", device = Devices.PIXEL_C)
@Composable
fun TestReceiptList() {
    AccountsTheme {
        ListOfReceiptImpl(
            navigateTo = {},
            changeSorting = {},
            state = ReceiptListState()
        )
    }
}
