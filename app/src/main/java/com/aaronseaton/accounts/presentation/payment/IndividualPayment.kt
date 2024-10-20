package com.aaronseaton.accounts.presentation.payment

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.openPDF
import com.aaronseaton.accounts.presentation.components.IndividualTransaction
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.util.fileLocation
import com.aaronseaton.accounts.util.fileName
import com.aaronseaton.accounts.util.htmlStringThree
import com.aaronseaton.accounts.util.saveHTMLAsPDF
import com.aaronseaton.accounts.util.tailWindStyle

@Composable
fun IndividualPayment(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    paymentID: String,
    viewModel: IndividualPaymentViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = paymentID) {
        viewModel.updateIndividualPaymentState(paymentID)
    }
    val state by viewModel.paymentIndividualState.collectAsState()
    val context = LocalContext.current
    val deletePayment = { it: FinancialTransaction -> viewModel.deletePayment(it) }
    val fileName = fileName(state.customer, state.payment)
    val fileLocation = fileLocation(fileName)
    val transactionAsHTML = htmlStringThree(
        tailWindStyle,
        state.customer,
        state.business,
        state.accountUser,
        state.payment,
    )
    val saveTransactionAsPDF = {
        saveHTMLAsPDF(
            context,
            transactionAsHTML,
            fileLocation,
        )
    }
    val openPDF = {
        openPDF(
            context,
            fileLocation,
        )
    }
    val savePdfAndOpen = {
        saveTransactionAsPDF()
        openPDF()
    }
    when (state.loading) {
        true -> LoadingScreen()
        false -> IndividualPaymentImpl(
            state.payment,
            state.customer,
            state.accountUser,
            state.business,
            navigateTo,
            popBackStack,
            savePdfAndOpen,
            viewModel::deletePayment
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualPaymentImpl(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    onFabPressed: () -> Unit,
    deletePayment: (FinancialTransaction) -> Unit,

    ) {
    val leftIcon = Icons.Default.ArrowBack
    val description = "Back"
    val title = "Payment No. ${transaction.documentID.take(4)}"
    var expanded by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = popBackStack) {
                            Icon(leftIcon, description)
                        }
                    },
                    actions = {
                        IconButton({ expanded = true }) {
                            Icon(Icons.Default.MoreVert, "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                {
                                    Text("Delete Expense")
                                },
                                onClick = {
                                    deletePayment(transaction)
                                    popBackStack()

                                }
                            )
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onFabPressed() }) {
                Text("Save & Open PDF", Modifier.padding(horizontal = 15.dp))
            }
        }
    ) { padding ->
        IndividualTransaction(
            transaction,
            customer,
            accountUser,
            business,
            navigateTo,
            Modifier.padding(padding)
        )
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun IndividualPaymentPreview() {
    AccountsTheme {
        IndividualPaymentImpl(
            transaction = TestInfo.firstPayment,
            customer = TestInfo.Damian,
            accountUser = User(),
            business = Business(),
            navigateTo = {},
            popBackStack = { /*TODO*/ },
            onFabPressed = { /*TODO*/ },
            deletePayment = {}
        )
    }
}