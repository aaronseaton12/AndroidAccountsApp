package com.aaronseaton.accounts.presentation.payment

import android.app.DatePickerDialog
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.aaronseaton.accounts.presentation.receipt.PaymentMethodDialog
import java.util.*

@Composable
fun EditPayment(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    paymentID: String,
    viewModel: IndividualPaymentViewModel = hiltViewModel()
) {
    viewModel.updateIndividualPaymentState(paymentID)
    val state by viewModel.paymentIndividualState.collectAsState()

    when (state.loading) {
        true -> LoadingScreen()
        false ->
            EditPaymentImpl(
                state.payment,
                state.customer,
                state.customers,
                navigateTo,
                popBackStack,
                viewModel::updatePayment
            )
    }
}


@Composable
private fun EditPaymentImpl(
    payment: Payment,
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit = {},
    updatePayment: (Payment) -> Unit
) {
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val title = "Add or Edit Expense"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) {
        EditPaymentContent(
            payment,
            initialCustomer,
            customers,
            navigateTo,
            updatePayment,
            Modifier.padding(it)
        )
    }
}

@Composable
fun EditPaymentContent(
    payment: Payment,
    initialCustomer: Customer,
    customers: List<Customer>,
    navigateTo: (String) -> Unit,
    updatePayment: (Payment) -> Unit,
    modifier: Modifier = Modifier
) {
    var paymentTransient by remember { mutableStateOf(payment) }
    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    var isDialogShowing by remember { mutableStateOf(false) }
    val onDismissRequest = { isDialogShowing = false }
    val onCustomerSelected: (Customer) -> Unit = {
        paymentTransient = paymentTransient.copy(customerID = it.documentID)
        selectedCustomer = it
    }

    fun setDate(date: Date, changeDate: (Date) -> Unit) =
        DatePickerDialog.OnDateSetListener { _, year, month, day ->
            changeDate(Date(year - 1900, month, day, date.hours, date.minutes))
        }

    val onPaymentMethodSelected: (String) -> Unit =
        { paymentTransient = paymentTransient.copy(payMethod = it) }
    var isPaymentDialogShowing by remember { mutableStateOf(false) }
    val onPaymentDismissRequest = { isPaymentDialogShowing = false }
    val context = LocalContext.current

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
            label = "Customer",
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
            updatePayment(paymentTransient)
            Toast.makeText(context, "Expense Updated", Toast.LENGTH_SHORT).show()
            navigateTo(Routes.PAYMENT_LIST)
        }
        ) {
            Text(text = "Update Expense")
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large screen", device = Devices.PIXEL_C)
@Composable
private fun EditPaymentPreview() {
    AccountsTheme {
        EditPaymentImpl(
            payment = TestInfo.firstPayment,
            navigateTo = {},
            initialCustomer = TestInfo.Damian,
            customers = listOf(TestInfo.Damian, TestInfo.Khadija),
            popBackStack = {},
            updatePayment = {}

        )
    }
}




