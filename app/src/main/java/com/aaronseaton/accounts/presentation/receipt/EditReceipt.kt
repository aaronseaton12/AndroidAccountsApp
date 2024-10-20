package com.aaronseaton.accounts.presentation.receipt

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.PaymentMethod
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.decimalFormat
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import com.aaronseaton.accounts.presentation.components.*
import com.aaronseaton.accounts.presentation.payment.CustomerDialog
import java.util.*

@Composable
fun EditReceipt(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    receiptID: String,
    viewModel: IndividualReceiptViewModel = hiltViewModel()
    //viewModel: TestReceiptVM = hiltViewModel()

) {
    LaunchedEffect(key1 = receiptID) {
        viewModel.updateIndividualReceiptState(receiptID)
    }

    val state by viewModel.individual.collectAsState()

    when (state.loading) {
        true -> LoadingScreen()
        false -> EditReceiptImpl(
            state.transaction,
            state.customer,
            state.customers,
            navigateTo,
            popBackStack,
            viewModel::updateReceipt
        )
    }
}


@Composable
private fun EditReceiptImpl(
    receipt: Receipt,
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit = {},
    updateReceipt: (Receipt) -> Unit
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val title = "Add or Edit Income Received"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) {
        EditReceiptContent(
            receipt,
            initialCustomer,
            customers,
            popBackStack,
            updateReceipt,
            Modifier.padding(it)
        )
    }
}

@Composable
fun EditReceiptContent(
    receipt: Receipt,
    initialCustomer: Customer,
    customers: List<Customer>,
    popBackStack: () -> Unit,
    updateReceipt: (Receipt) -> Unit,
    modifier: Modifier = Modifier
) {
    var receiptTransient by remember { mutableStateOf(receipt) }
    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    var isDialogShowing by remember { mutableStateOf(false) }
    val onDismissRequest = { isDialogShowing = false }
    val onCustomerSelected: (Customer) -> Unit = {
        receiptTransient = receiptTransient.copy(customerID = it.documentID)
        selectedCustomer = it
    }

    fun setDate(date: Date, changeDate: (Date) -> Unit) =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            changeDate(Date(year - 1900, month, day, date.hours, date.minutes))
        }

    val onPaymentMethodSelected: (String) -> Unit =
        { receiptTransient = receiptTransient.copy(payMethod = it) }
    var isPaymentDialogShowing by remember { mutableStateOf(false) }
    val onPaymentDismissRequest = { isPaymentDialogShowing = false }
    val context = LocalContext.current

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
        var amount by remember { mutableStateOf(decimalFormat.format(receiptTransient.amount)) }
        EditOrAddNumberField(
            value = amount,
            label = "Amount"
        ) {
            amount = it
            if (!amount.isValidDoubleString(decimalFormat)) {
                Toast.makeText(context, "Input a valid number", Toast.LENGTH_SHORT)
                    .show()
                return@EditOrAddNumberField
            }
            receiptTransient = receiptTransient.copy(
                amount = decimalFormat.parse(amount)
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
            updateReceipt(receiptTransient)
            Toast.makeText(context, "Income Updated", Toast.LENGTH_SHORT).show()
            popBackStack()
        }
        ) {
            Text(text = "Update Income")
        }
    }
}

//@Preview
//@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Preview("Large Screen", device = Devices.PIXEL_C)
//@Composable
//private fun EditReceiptPreview() {
//    AccountsTheme {
//        EditReceiptImpl(
//            receipt = TestInfo.firstReceipt,
//            navigateTo = {},
//            initialCustomer = TestInfo.Damian,
//            customers = listOf(TestInfo.Damian, TestInfo.Khadija),
//            popBackStack = {},
//            updateReceipt = {}
//
//        )
//    }
//}


