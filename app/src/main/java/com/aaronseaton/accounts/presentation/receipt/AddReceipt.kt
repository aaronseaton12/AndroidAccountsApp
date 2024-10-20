package com.aaronseaton.accounts.presentation.receipt

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.aaronseaton.accounts.domain.model.PaymentMethod
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.TestInfo
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import com.aaronseaton.accounts.presentation.components.*
import com.aaronseaton.accounts.presentation.payment.CustomerDialog
import java.util.*


@Composable
fun AddReceipt(
    navigateTo: (String) -> Unit = {},
    customerID: String? = "",
    viewModel: AddReceiptViewModel = hiltViewModel()
) {
    val uiState by viewModel.list.collectAsState()
    when (uiState.loading) {
        true -> LoadingScreen()
        false -> AddReceiptImpl(
            initialCustomer = uiState.customers.find { it.documentID == customerID } ?: Customer(),
            customers = uiState.customers,
            navigateTo = navigateTo,
            insertReceipt = viewModel::insertReceipt
        )
    }
}


@Composable
private fun AddReceiptImpl(
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    insertReceipt: (Receipt) -> Unit,
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { }
    val title = "Add or Edit Receipt"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) {
        AddReceiptContent(
            initialCustomer,
            customers,
            navigateTo,
            insertReceipt,
            Modifier.padding(it)
        )
    }
}

@Composable
fun AddReceiptContent(
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    insertReceipt: (Receipt) -> Unit,
    modifier: Modifier = Modifier
) {
    var receiptTransient by remember {
        mutableStateOf(
            Receipt(
                customerID = initialCustomer.documentID,
                payMethod = PaymentMethod.CASH.type
            )
        )
    }
    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    val onCustomerSelected: (Customer) -> Unit = {
        receiptTransient = receiptTransient.copy(customerID = it.documentID)
        selectedCustomer = it
    }

    fun setDate(date: Date, changeDate: (Date) -> Unit) =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            changeDate(Date(year - 1900, month, day, date.hours, date.minutes))
        }

    val context = LocalContext.current
    val onPaymentMethodSelected: (String) -> Unit =
        { receiptTransient = receiptTransient.copy(payMethod = it) }
    var isPaymentDialogShowing by remember { mutableStateOf(false) }
    val onPaymentDismissRequest = { isPaymentDialogShowing = false }
    var isDialogShowing by remember { mutableStateOf(false) }
    val onDismissRequest = { isDialogShowing = false }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState(0)),
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
            paymentMethod = receiptTransient.payMethod,
            onPaymentMethodSelected = onPaymentMethodSelected
        )
        ClickableTextField(
            value = selectedCustomer.fullName(),
            onValueChange = {},
            label = "Customer",
            modifier = Modifier.clickable { isDialogShowing = true }
        )
        //EditOrAddNumberField(customerID, "CustomerID") { customerID = it }
        var amount by remember { mutableStateOf(Util.decimalFormat.format(receiptTransient.amount)) }
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
            receiptTransient = receiptTransient.copy(
                amount = Util.decimalFormat.parse(amount)
                    ?.toDouble() ?: 0.0
            )

        }
        ClickableTextField(
            value = dateFormatter.format(receiptTransient.date),
            onValueChange = { },
            label = "Date",
            modifier = Modifier.clickable {
                showDatePicker(
                    context,
                    receiptTransient.date,
                    setDate(
                        receiptTransient.date,
                        changeDate = { receiptTransient = receiptTransient.copy(date = it) }
                    )
                )
            }
        )
        ClickableTextField(
            value = receiptTransient.payMethod ?: PaymentMethod.CASH.type,
            onValueChange = { receiptTransient = receiptTransient.copy(payMethod = it) },
            label = "Payment Method",
            modifier = Modifier.clickable { isPaymentDialogShowing = true }
        )
        EditOrAddTextField(
            name = receiptTransient.reason ?: "",
            label = "Reason"
        ) { receiptTransient = receiptTransient.copy(reason = it) }

        //Add Matter
        //Matter has a customerID and a name and a type

        Spacer(modifier = Modifier.padding(5.dp))
        Button(onClick = {
            if (selectedCustomer.documentID.isNullOrBlank()) {
                Toast.makeText(context, "Select a Contact", Toast.LENGTH_SHORT).show()
                return@Button
            }
            insertReceipt(receiptTransient)
            Toast.makeText(context, "Receipt Added", Toast.LENGTH_SHORT).show()
            navigateTo(Routes.RECEIPT_LIST)

        }
        ) {
            Text(text = "Add Receipt")
        }
    }
}

@Composable
fun PaymentMethodDialog(
    isPaymentDialogShowing: Boolean,
    onPaymentDismissRequest: () -> Unit,
    paymentMethod: String?,
    onPaymentMethodSelected: (String) -> Unit
) {
    if (isPaymentDialogShowing)
        Dialog(onPaymentDismissRequest) {
            Column {
                LazyColumn(Modifier.padding(bottom = 60.dp)) {

                    val paymentMethods = PaymentMethod.values()
                    items(paymentMethods) { method ->
                        PaymentMethodPicker(
                            method.type,
                            onPaymentMethodSelected,
                            onPaymentDismissRequest
                        )
                    }
                }
            }
        }

}

@Composable
fun PaymentMethodPicker(
    method: String,
    onPaymentMethodSelected: (String) -> Unit,
    onPaymentDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPaymentMethodSelected(method)
                onPaymentDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {

            Text(
                text = method,
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun AddReceiptPreview() {
    AccountsTheme {
        AddReceiptImpl(
            initialCustomer = Customer(),
            customers = listOf(TestInfo.Damian, TestInfo.Khadija),
            {}
        ) {}
    }

}


