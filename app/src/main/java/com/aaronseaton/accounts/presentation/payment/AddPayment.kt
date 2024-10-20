package com.aaronseaton.accounts.presentation.payment

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.PaymentMethod
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import com.aaronseaton.accounts.presentation.components.*
import com.aaronseaton.accounts.presentation.customer.SearchAppBar
import com.aaronseaton.accounts.presentation.receipt.PaymentMethodDialog
import java.util.*

@Composable
fun AddPayment(
    customerID: String? = "",
    navigateTo: (String) -> Unit = {},
    viewModel: PaymentViewModels = hiltViewModel()
) {
    val uiState by viewModel.paymentState.collectAsState()
    //val customers by accountViewModel.customers.observeAsState(emptyList())
    val initialCustomer = uiState.customers.find { it.documentID == customerID } ?: Customer()
    val insertPayment = { payment: Payment -> viewModel.insertPayment(payment) }
    when (uiState.loading) {
        true -> LoadingScreen()
        false -> AddPaymentImpl(
            initialCustomer = initialCustomer,
            customers = uiState.customers,
            insertPayment = insertPayment,
            navigateTo = navigateTo
        )
    }
}


@Composable
private fun AddPaymentImpl(
    initialCustomer: Customer,
    customers: List<Customer>,
    insertPayment: (Payment) -> Unit,
    navigateTo: (String) -> Unit
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val title = "Add or Edit Expense"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) { padding ->
        println(initialCustomer)
        PaymentInputContent(
            initialCustomer,
            customers,
            navigateTo,
            insertPayment,
            Modifier.padding(padding)
        )
    }
}

@Composable
fun PaymentInputContent(
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    insertPayment: (Payment) -> Unit,
    modifier: Modifier = Modifier
) {
    var paymentTransient by remember { mutableStateOf(Payment(customerID = initialCustomer.documentID)) }
    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    val onCustomerSelected: (Customer) -> Unit = {
        paymentTransient = paymentTransient.copy(customerID = it.documentID)
        selectedCustomer = it
    }

    fun setDate(date: Date, changeDate: (Date) -> Unit) =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            changeDate(Date(year - 1900, month, day, date.hours, date.minutes))
        }

    val context = LocalContext.current
    val onPaymentMethodSelected: (String) -> Unit =
        { paymentTransient = paymentTransient.copy(payMethod = it) }
    var isPaymentDialogShowing by remember { mutableStateOf(false) }
    val onPaymentDismissRequest = { isPaymentDialogShowing = false }
    var isDialogShowing by remember { mutableStateOf(false) }
    val onDismissRequest = { isDialogShowing = false }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomerDialog(
            isDialogShowing = isDialogShowing,
            onDismissRequest = onDismissRequest,
            customers = customers,
            onCustomerSelected = onCustomerSelected
        )
        PaymentMethodDialog(
            isPaymentDialogShowing = isPaymentDialogShowing,
            onPaymentDismissRequest = onPaymentDismissRequest,
            paymentMethod = paymentTransient.payMethod,
            onPaymentMethodSelected = onPaymentMethodSelected
        )
        ClickableTextField(
            value = selectedCustomer.fullName(),
            onValueChange = {},
            label = "Customer Name",
            modifier = Modifier.clickable { isDialogShowing = true }
        )
        //EditOrAddNumberField(customerID, "CustomerID") { customerID = it }
        var amount by remember { mutableStateOf(Util.decimalFormat.format(paymentTransient.amount)) }
        EditOrAddNumberField(
            value = amount,
            label = "Amount"
        ) {
            amount = it
            if (!amount.isValidDoubleString(Util.decimalFormat)) {
                Toast.makeText(context, "Input a valid number", Toast.LENGTH_SHORT)
                    .show()
                return@EditOrAddNumberField
            }
            paymentTransient = paymentTransient.copy(
                amount = Util.decimalFormat.parse(amount)
                    ?.toDouble() ?: 0.0
            )

        }
        ClickableTextField(
            value = dateFormatter.format(paymentTransient.date),
            onValueChange = {},
            label = "Date",
            modifier = Modifier.clickable {
                showDatePicker(
                    context,
                    paymentTransient.date,
                    setDate(
                        paymentTransient.date,
                        changeDate = { paymentTransient = paymentTransient.copy(date = it) }
                    )
                )
            }
        )
        ClickableTextField(
            value = paymentTransient.payMethod ?: PaymentMethod.CASH.type,
            onValueChange = { paymentTransient = paymentTransient.copy(payMethod = it) },
            label = "Payment Method",
            modifier = Modifier.clickable { isPaymentDialogShowing = true }
        )
        EditOrAddTextField(
            name = paymentTransient.reason ?: "",
            label = "Reason"
        ) { paymentTransient = paymentTransient.copy(reason = it) }

        //Add Matter
        //Matter has a customerID and a name and a type

        Spacer(modifier = Modifier.padding(5.dp))
        Button(onClick = {
            if (selectedCustomer.documentID.isBlank()) {
                Toast.makeText(context, "Select a Customer", Toast.LENGTH_SHORT).show()
                return@Button
            }
            insertPayment(paymentTransient)
            Toast.makeText(context, "Expense Added", Toast.LENGTH_SHORT).show()
            navigateTo(Routes.PAYMENT_LIST)
        }
        ) {
            Text(text = "Add Expense")
        }
    }
}

@Composable
fun CustomerDialog(
    isDialogShowing: Boolean,
    onDismissRequest: () -> Unit,
    customers: List<Customer>,
    onCustomerSelected: (Customer) -> Unit
) {
    var searchAppText by remember { mutableStateOf("") }

    if (isDialogShowing) {
        Dialog(onDismissRequest) {
            Column {
                Row {
                    SearchAppBar(
                        text = searchAppText,
                        onTextChange = { searchAppText = it },
                        onCloseClicked = { onDismissRequest() },
                        onSearchClicked = { Log.d("Searched Text", it) }
                    )
                }
                LazyColumn(Modifier.padding(bottom = 60.dp)) {

                    val filteredCustomers = customers
                        .filter {
                            it.firstName.contains(
                                searchAppText,
                                ignoreCase = true
                            ) || it.lastName.contains(
                                searchAppText,
                                ignoreCase = true
                            )
                        }
                        .sortedBy {
                            it.firstName.first()
                        }
                    items(filteredCustomers) { customer ->
                        CustomerCardPicker(
                            customer,
                            onCustomerSelected,
                            onDismissRequest
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerCardPicker(
    customer: Customer,
    onCustomerSelected: (Customer) -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCustomerSelected(customer)
                onDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {

            Text(
                text = customer.fullName(),
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}


@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun AddPaymentPreview() {
    AccountsTheme {
        AddPaymentImpl(
            initialCustomer = Customer(),
            customers = listOf(TestInfo.Damian, TestInfo.Khadija),
            insertPayment = {},
            navigateTo = {}
        )
    }
}