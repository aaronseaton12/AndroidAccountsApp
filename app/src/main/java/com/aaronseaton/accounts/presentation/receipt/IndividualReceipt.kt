package com.aaronseaton.accounts.presentation.receipt

import android.content.res.Configuration
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
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.openPDF
import com.aaronseaton.accounts.presentation.components.IndividualTransaction
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.util.fileLocation
import com.aaronseaton.accounts.util.fileName
import com.aaronseaton.accounts.util.htmlStringThree
import com.aaronseaton.accounts.util.saveHTMLAsPDF
import com.aaronseaton.accounts.util.tailWindStyle

const val TAG = "Individual Income"
@Composable
fun IndividualReceipt(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    receiptID: String,
    viewModel: IndividualReceiptViewModel = hiltViewModel()
    //viewModel: TestReceiptVMI n = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(key1 = receiptID) {
        viewModel.updateIndividualReceiptState(receiptID)
    }
    val state by viewModel.individual.collectAsState()
    val context = LocalContext.current
    val deletePayment = { it: FinancialTransaction -> viewModel.deleteReceipt(it) }
    val fileName = fileName(state.customer, state.transaction)
    val fileLocation = fileLocation(fileName)
    val transactionAsHTML = htmlStringThree(
        tailWindStyle,
        state.customer,
        state.business,
        state.accountUser,
        state.transaction,
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
        false -> IndividualReceiptImpl(
            state.transaction,
            state.customer,
            state.accountUser,
            state.business,
            navigateTo,
            popBackStack,
            savePdfAndOpen,
            viewModel::deleteReceipt
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualReceiptImpl(
    transaction: FinancialTransaction,
    customer: Customer,
    accountUser: User,
    business: Business,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    onFabPressed: () -> Unit,
    deleteReceipt: (Receipt) -> Unit,
) {
    val description = "Back"
    val title = "Receipt No. ${transaction.documentID.take(4)}"
    var expanded by remember { mutableStateOf(false) }
    val leftIcon = Icons.Default.ArrowBack

    Scaffold(
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
                                    Text("Delete Income")
                                },
                                onClick = {
                                    deleteReceipt(transaction as Receipt)
                                    navigateTo(Routes.RECEIPT_LIST)
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
private fun IndividualReceiptPreview() {
    AccountsTheme {

        IndividualReceiptImpl(
            transaction = TestInfo.firstReceipt,
            customer = TestInfo.Damian,
            accountUser = User(),
            business = Business(),
            navigateTo = {},
            popBackStack = { /*TODO*/ },
            onFabPressed = { /*TODO*/ },

            ) {}
    }
}



