package com.aaronseaton.accounts.presentation.customer

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Sorting
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.SearchWidgetState
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen

@Composable
fun ListOfCustomers(
    navigateTo: (String) -> Unit = {},
    viewModel: CustomerViewModels = hiltViewModel(),
) {
    val state by viewModel.customerListState.collectAsState(CustomerListState(loading = true))
    val searchBar by viewModel.searchBar.collectAsState(CustomerListState(loading = true))
    "List Of Customers".also { println(it) }
    ListCustomersImpl(
        viewModel::onQueryChange,
        viewModel::onCloseClicked,
        viewModel::onOpenClicked,
        viewModel::changeSorting,
        navigateTo,
        searchBar.searchState,
        state.customers,
        searchBar.query,
        state.loading,
        searchBar.sorting
    )
}

@Composable
fun ListCustomersImpl(
    onQueryChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onOpenClicked: () -> Unit,
    changeSorting: (Sorting.CustomerSorting) -> Unit = {},
    navigateTo: (String) -> Unit,
    searchState: SearchWidgetState,
    customers: List<Customer>,
    query: String,
    loading: Boolean,
    sorting: Sorting.CustomerSorting,

    ) {
    val fabIcon = Icons.Default.Add
    val onFabPressed = { navigateTo(Routes.ADD_CUSTOMER) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainAppBar(
                navigateTo = navigateTo,
                searchWidgetState = searchState,
                searchTextState = query,
                onTextChange = onQueryChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = { Log.d("Searched Text", it) },
                onSearchTriggered = onOpenClicked,
                changeSorting = changeSorting
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Contacts") },
                icon = { Icon(fabIcon, "Add Contact") },
                onClick = onFabPressed
            )
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.CUSTOMER_LIST) }
    ) { paddingValues ->
        when (loading) {
            true -> LoadingScreen()
            false -> OutputArea(
                customers,
                navigateTo,
                sorting,
                query,
                Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun OutputArea(
    customers: List<Customer>,
    navigateTo: (String) -> Unit = {},
    sorting: Sorting.CustomerSorting,
    query: String,
    modifier: Modifier
) {
    val filteredCustomers = customers.filter {
        it.firstName.contains(query, ignoreCase = true) || it.lastName.contains(
            query,
            ignoreCase = true
        )
    }
    val filteredSortedCustomers = when (sorting) {
        Sorting.CustomerSorting.BY_CUSTOMER_ID -> filteredCustomers.sortedBy { it.customerID }
        Sorting.CustomerSorting.BY_FIRST_NAME -> filteredCustomers.sortedBy { it.firstName }
        Sorting.CustomerSorting.BY_LAST_NAME -> filteredCustomers.sortedBy { it.lastName }
        //else -> filteredCustomers
    }
    if (filteredCustomers.isEmpty()) {
        Text("There is no contact named \'$query\'")
    } else {
        LazyColumn(contentPadding = PaddingValues(bottom = 150.dp), modifier = modifier) {
            items(filteredSortedCustomers) { customer ->
                CustomerCard(customer, navigateTo)
            }
        }
    }
}

@Composable
fun MainAppBar(
    navigateTo: (String) -> Unit = {},
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit,
    changeSorting: (Sorting.CustomerSorting) -> Unit = {}
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                navigateTo = navigateTo,
                onSearchClicked = onSearchTriggered,
                changeSorting = changeSorting
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppBar(
    navigateTo: (String) -> Unit = {},
    onSearchClicked: () -> Unit,
    changeSorting: (Sorting.CustomerSorting) -> Unit = {}
) {
    val onPressed = { navigateTo(Routes.HOME) }
    val icon = Icons.Filled.ArrowBack
    val description = "Back"
    val title = "List of Contacts"
    var expanded by remember { mutableStateOf(false) }
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
                IconButton(
                    onClick = { onSearchClicked() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Search")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = {
                            Text("Sort By First Name")
                        },
                        onClick = { changeSorting(Sorting.CustomerSorting.BY_FIRST_NAME) }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Sort By Last Name")
                        },
                        onClick = { changeSorting(Sorting.CustomerSorting.BY_LAST_NAME) }
                    )
                    DropdownMenuItem(
                        text = {
                            Text("Sort By Contact ID")
                        },
                        onClick = { changeSorting(Sorting.CustomerSorting.BY_CUSTOMER_ID) }
                    )
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(
    text: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        tonalElevation = 5.dp,
    ) {
        TextField(modifier = Modifier
            .fillMaxWidth(),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            placeholder = {
                Text(
                    text = "Search here...",
                )
            },
            textStyle = MaterialTheme.typography.titleMedium,
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                    )
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            onTextChange("")
                        } else {
                            onCloseClicked()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Icon"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors()
        )
    }
}
//
//@Preview
//@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Preview("Large Screen", device = Devices.PIXEL_C)
//@Composable
//fun TestListCustomers() {
//    AccountsTheme {
//        ListCustomersImpl(
//            customers = TestInfo.listOfCustomers,
//            query = "",
//            onQueryChange = {},
//            searchState = SearchWidgetState.CLOSED,
//            onCloseClicked = { /*TODO*/ },
//            onOpenClicked = { /*TODO*/ },
//            sorting = CustomerSorting.BY_FIRST_NAME,
//            navigateTo = {},
//            customerState = CustomerList()
//        )
//
//    }
//}
