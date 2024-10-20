package com.aaronseaton.accounts.presentation.customer

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.CompoundFAB
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.payment.PaymentCard
import com.aaronseaton.accounts.presentation.receipt.ReceiptCard

@Composable
fun IndividualCustomer(
    customerID: String,
    navigateTo: (String) -> Unit = {},
    viewModel: IndividualCustomerViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = customerID) {
        viewModel.updateIndividualCustomerState(customerID)
    }

    val individualState by viewModel.individualCustomerState.collectAsState()

    IndividualCustomerImpl(
        individualState,
        navigateTo,
    )
}


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
    val title = ""
    val leftIcon = Icons.Default.ArrowBack
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
            false -> Column(Modifier.padding(it)) {
                CustomerInformation(state.customer, navigateTo)
                CustomerTransactions(state.customer, state.transactions, navigateTo)
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
    val (_, firstName, _, lastName, email, address, phoneNumber) = customer
    val dial = {
        Intent(Intent.ACTION_DIAL).let {
            it.data = Uri.parse("tel:${phoneNumber.cellNumber}")
            startActivity(context, it, null)
        }
    }
    val message = {
        Intent(Intent.ACTION_SENDTO).let {
            it.data = Uri.parse("sms:${phoneNumber.cellNumber}")
            startActivity(context, it, null)
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        //.clickable {navController.navigate("edit_payment/$paymentID")}
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                text = "$firstName $lastName",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                textAlign = TextAlign.Center
            )
            Text(
                text = phoneNumber.cellNumber.ifBlank { "Please add phone #" },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = email.ifBlank { "Please add email" },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = address.toString().ifBlank { "Please add address" },
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = dial) { Text("Call Client") }
                Button(onClick = message) { Text("Text Client") }
                Button(onClick = {
                    navigateTo(Routes.EDIT_CUSTOMER + "/${customer.documentID}")
                })
                { Text("Edit Client") }
            }
            Spacer(modifier = Modifier.height(15.dp))
            //Divider(thickness = 1.dp, color = Color.LightGray)
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
            if (transaction is Receipt)
                ReceiptCard(customer, transaction, navigateTo)
            else if (transaction is Payment)
                PaymentCard(customer, transaction, navigateTo)
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun IndividualCustomerPreview() {
    AccountsTheme {
        IndividualCustomerImpl(
            IndividualCustomerState(
                customer = TestInfo.Damian,
                transactions = (TestInfo.listOfTestPayments + TestInfo.listOfTestReceipts),
            )
        )
    }
}




