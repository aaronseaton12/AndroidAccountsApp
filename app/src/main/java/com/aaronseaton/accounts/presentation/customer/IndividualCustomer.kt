package com.aaronseaton.accounts.presentation.customer

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.CompoundFAB
import com.aaronseaton.accounts.presentation.components.FinancialSummary
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.TransactionCard
import com.aaronseaton.accounts.presentation.matter.MatterCard
import com.aaronseaton.accounts.presentation.components.itemList
import com.aaronseaton.accounts.presentation.task.TaskCard
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes

@Composable
fun IndividualCustomer(
    customerID: String,
    navigateTo: (String) -> Unit = {},
    viewModel: CustomerViewModels = hiltViewModel()
) {
    val individualState by viewModel
        .individualCustomerState(customerID)
        .collectAsState(IndividualCustomerState(loading = true))

    IndividualCustomerImpl(
        individualState,
        navigateTo,
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualCustomerImpl(
    state: IndividualCustomerState,
    navigateTo: (String) -> Unit = {},
) {
    Log.d("Single Customer", state.customer.toString())
    var showThreeButtons by remember { mutableStateOf(false) }
    val fabText = if (showThreeButtons) "Close This" else "Transactions"
    val fabIcon = if (showThreeButtons) Icons.Default.Close else Icons.Default.Add
    val fabModifier = Modifier
        .width(160.dp)
        .padding(2.dp)
    //.sizeIn(130.dp, 50.dp, 200.dp, 100.dp)
    val title = "Customer"
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val onFabPressed = { showThreeButtons = !showThreeButtons }
    val onAddReceipt = { navigateTo(Routes.ADD_RECEIPT + "/" + state.customer.documentID) }
    val onAddPayment = { navigateTo(Routes.ADD_PAYMENT + "/" + state.customer.documentID) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
        bottomBar = { AllBottomBar(navigateTo) },
        floatingActionButton =
        {
            CompoundFAB(
                fabLabel = fabText,
                fabIcon = fabIcon,
                onTopFabPressed = onAddReceipt,
                onBottomFabPressed = onAddPayment,
                topFabLabel = "Add Income",
                bottomFabLabel = "Add Expense"
            )
        }
    ) {
        when(state.loading){
            true -> LoadingScreen()
            false -> Column(
                Modifier
                    .padding(it)
                    .padding(top = 5.dp)) {
                CustomerInformation(state.customer, navigateTo)
                FinancialSummary(
                    receipts = state.transactions.filterIsInstance<Receipt>(),
                    payments = state.transactions.filterIsInstance<Payment>(),
                    modifier = Modifier.padding(10.dp)
                )
                Column {
                    val finance = "Finance"
                    val tasks = "Tasks"
                    val matters = "Matters"
                    val titles = listOf(matters, finance, tasks)
                    var tabState by remember { mutableStateOf(titles[1]) }
                    Column {
                        SecondaryTabRow(selectedTabIndex = titles.indexOf(tabState)) {
                            titles.forEach { title ->
                                Tab(
                                    selected = tabState == title,
                                    onClick = { tabState = title },
                                    text = {
                                        Text(
                                            text = title,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                )
                            }
                        }
                       when (tabState) {
                            matters -> {
                                LazyColumn (Modifier.fillMaxSize()) {
                                    itemList(state.matters, "Matters"){ matter ->
                                        MatterCard(matter = matter, navigateTo = navigateTo)
                                    }
                                }
                            }
                            finance -> {
                                LazyColumn(Modifier.fillMaxSize()) {
                                    itemList(state.transactions, "Transactions"){ transaction ->
                                        TransactionCard(
                                            transaction = transaction,
                                            customer = state.customer,
                                            accountUser = state.accountUser,
                                            business = Business(),
                                            navigateTo = navigateTo
                                        )
                                    }
                                }
                            }

                            tasks -> {
                                LazyColumn {
                                    itemList(state.tasks, "Tasks") { task ->
                                        TaskCard(
                                            task = task,
                                            navigateTo = navigateTo, updateTask = {})
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomerInformation(
    customer: Customer,
    navigateTo: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val dial = {
        Intent(Intent.ACTION_DIAL).let {
            it.data = Uri.parse("tel:${customer.phoneNumber.cellNumber}")
            startActivity(context, it, null)
        }
    }
    val message = {
        Intent(Intent.ACTION_SENDTO).let {
            it.data = Uri.parse("sms:${customer.phoneNumber.cellNumber}")
            startActivity(context, it, null)
        }
    }
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
    )
    var expanded by remember{mutableStateOf(false)}
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 0.25.dp,
    ) {
        Row {
            Column (modifier = Modifier.weight(10.0f).padding(8.dp)){
                Text(
                    text = customer.fullName(),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                )
                Text(
                    text = customer.phoneNumber.cellNumber.ifBlank { "Please add phone #" },
                    modifier = Modifier.fillMaxWidth(),
                    style = labelStyle,
                )
                Text(
                    text = customer.emailAddress.ifBlank { "Please add email" },
                    modifier = Modifier.fillMaxWidth(),
                    style = labelStyle,
                )
                Text(
                    text = customer.address.toString().ifBlank { "Please add address" },
                    modifier = Modifier.fillMaxWidth(),
                    style = labelStyle,
                )
            }

            Column (Modifier.weight(1.0f)){
                IconButton(
                    onClick = { expanded = !expanded}) {
                    Icon(
                        Icons.Default.MoreVert,
                        tint = MaterialTheme.colorScheme.onSurface,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Call Client") },
                        onClick = dial
                    )
                    DropdownMenuItem(
                        text = { Text("Message Client") },
                        onClick = message
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Client") },
                        onClick = {
                            navigateTo(Routes.EDIT_CUSTOMER + "/${customer.documentID}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerTransactions(
    customer: Customer,
    allTransactions: List<FinancialTransaction>,
    navigateTo: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        items(allTransactions) { transaction ->
            TransactionCard(
                transaction = transaction,
                customer = customer,
                accountUser = User(),
                business = Business(),
                navigateTo = navigateTo
            )
        }
    }
}

@Preview(apiLevel = 33)
@Preview(apiLevel = 33, name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun IndividualCustomerPreview() {
    AccountsTheme {
        IndividualCustomerImpl(
            state = IndividualCustomerState(
                customer = TestInfo.Damian,
                //transactions = TestInfo.listOfTestPayments + TestInfo.listOfTestReceipts,
                loading = false
            )
        )
    }
}




