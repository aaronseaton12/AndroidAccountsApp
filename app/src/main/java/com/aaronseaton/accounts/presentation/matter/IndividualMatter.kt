package com.aaronseaton.accounts.presentation.matter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.CompoundFabNew
import com.aaronseaton.accounts.presentation.components.FinancialSummary
import com.aaronseaton.accounts.presentation.components.HeaderComponent
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.TransactionCard
import com.aaronseaton.accounts.presentation.components.itemList
import com.aaronseaton.accounts.presentation.task.TaskCard
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.format

@Composable
fun IndividualMatter(
    matterID: String? = null,
    navigateTo: (String) -> Unit = {},
    viewModel: MatterViewModels = hiltViewModel()
) {
    LaunchedEffect(matterID) {viewModel.setMatterId(matterID)}
    val state by viewModel.individualState.collectAsState(IndividualMatterState())

    when (state.loading) {
        true -> LoadingScreen()
        false -> IndividualMattersImpl(
            state,
            navigateTo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualMattersImpl(state: IndividualMatterState, navigateTo: (String) -> Unit) {
    val icon = Icons.AutoMirrored.Filled.ArrowBack
    val onHomeButtonClicked = { navigateTo(Routes.HOME) }
    val title = "Individual Matter"
    val description = "Back"
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
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.INDIVIDUAL_PAYMENT) },
        floatingActionButton = {
            CompoundFabNew(
                name = "Add",
                icon = Icons.Default.Add,
                fabs = listOf (
                    Pair({ navigateTo(Routes.ADD_PAYMENT+ "/ ") },"Add Payment"),
                    Pair({ navigateTo(Routes.ADD_RECEIPT+ "/ ") }, "Add Receipt"),
                    Pair({ navigateTo(Routes.ADD_TASK) }, "Add Task")
                )
            )
        }
    ) {
        when (state.loading) {
            true -> LoadingScreen()
            false -> IndividualMattersContent(state, navigateTo, Modifier.padding(it))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualMattersContent(
    state: IndividualMatterState,
    navigateTo: (String) -> Unit,
    modifier: Modifier
) {
    val transactions = state.receipts + state.payments
    val labelStyle = MaterialTheme.typography.bodyLarge.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.9
    )
    val lightness = if (isSystemInDarkTheme()) 0.8F else 0.45F

    Column(modifier) {
        Surface (tonalElevation = 0.25.dp) {
            Column(Modifier.padding(10.dp), Arrangement.spacedBy(15.dp)) {
                HeaderComponent(
                    modifier = Modifier,
                    title = "Matter - ${Util.paymentFormat.format(state.matter.number)}",
                    actionIcon = { Icon(
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        imageVector = Icons.Default.MoreVert
                    ) },
                    menuItems = {
                        DropdownMenuItem(
                            text = { Text("Print") },
                            onClick = { }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { }
                        )

                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = { }
                        )
                    }
                ) {
                    Text(
                        text = state.matter.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.W400)
                    )
                    Text(
                        text = state.matter.description,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 0.8
                        )
                    )
                }
                FinancialSummary(receipts = state.receipts, payments = state.payments)
            }
        }

        Column {
            val finance = "Finance"
            val tasks = "Tasks"
            val titles = listOf(finance, tasks)
            var tabState by remember { mutableStateOf(titles[0]) }
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
                    finance -> {
                        LazyColumn(Modifier.fillMaxSize()){
                            itemList(transactions, "Transactions"){ transaction ->
                                TransactionCard(
                                    transaction = transaction,
                                    customer = state.customers.single { customer->
                                        customer.documentID == transaction.customerID },
                                    accountUser = state.accountUser,
                                    business = Business(),
                                    navigateTo = navigateTo
                                )
                            }
                        }
                    }
                    tasks -> {
                        LazyColumn( Modifier.fillMaxSize() ) {
                            itemList(state.tasks, "Tasks"){
                                TaskCard(
                                    task = it,
                                    navigateTo = navigateTo,
                                    updateTask = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatterTransaction(
    customer: Customer,
    transaction: FinancialTransaction,
    navigateTo: (String) -> Unit, modifier:
    Modifier = Modifier
) {

    val route = when (transaction) {
        is Receipt -> Routes.INDIVIDUAL_RECEIPT
        is Payment -> Routes.INDIVIDUAL_PAYMENT
        else -> "Transaction"
    }
    val transactionName = when (transaction) {
        is Receipt -> "Receipt"
        is Payment -> "Payment"
        else -> "Transaction"
    }

    val labelStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
    )
    val valueStyle = MaterialTheme.typography.titleSmall
    val labelModifier = Modifier.padding(bottom = 10.dp)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { navigateTo(route + "/" + transaction.documentID) },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            tonalElevation = 1.dp,
            //shadowElevation = 3.dp,
            shape = MaterialTheme.shapes.small,
            //border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(

                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(
                        vertical = 8.dp,
                        horizontal = 4.dp
                    )
                    .widthIn(max = 450.dp)
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "$${transaction.amount.format()}",
                        textAlign = TextAlign.Left,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = customer.fullName(),
                        textAlign = TextAlign.Center,
                        style = labelStyle,
                        //modifier = labelModifier
                    )
                    Text(
                        text = transaction.reason ?: "",
                        textAlign = TextAlign.Left,
                        style = labelStyle,
                        modifier = labelModifier.padding(top = 0.dp)
                    )
                    //AccountDivider()
                    Spacer(Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = Util.dateFormatter.format(transaction.date),
                                textAlign = TextAlign.Left,
                                style = valueStyle
                            )
                            Text(
                                text = "Dated",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }
                        Column {
                            Text(
                                text = transaction.payMethod ?: "-",
                                textAlign = TextAlign.Left,
                                style = valueStyle
                            )
                            Text(
                                text = "Method",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }
                        Column {
                            Text(
                                text = "$transactionName ‧ ${transaction.documentID.take(4)}",
                                textAlign = TextAlign.Center,
                                style = valueStyle
                            )

                            Text(
                                text = "Amount",
                                textAlign = TextAlign.Left,
                                style = labelStyle,
                                modifier = labelModifier
                            )
                        }

                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text("Print $transactionName")
                    }
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Email $transactionName")
                    }
                    TextButton(
                        onClick = { },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Edit")
                    }
                }
            }
        }
    }
}

@Preview (apiLevel = 31)
@Composable
fun IndividualMatterPreview () {
    AccountsTheme {
        IndividualMattersImpl(
            state = IndividualMatterState().copy(
                matter = TestInfo.matters[1],
                //receipts = TestInfo.listOfTestReceipts,
                //payments = TestInfo.listOfTestPayments,
                customers = TestInfo.listOfCustomers,
                loading = false,
                tasks = listOf(
                    Task(
                        documentID = "AaO",
                        name = "This",
                        description = "This description",
                    )
                )
            ),
            navigateTo = {}
        )
    }
}