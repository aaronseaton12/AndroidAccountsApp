package com.aaronseaton.accounts.presentation.receipt

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.PaymentMethod
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.ClickableTextField
import com.aaronseaton.accounts.presentation.components.EditOrAddNumberField
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.ItemSelect
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.showDatePicker
import com.aaronseaton.accounts.presentation.matter.MatterListState
import com.aaronseaton.accounts.presentation.matter.MatterViewModels
import com.aaronseaton.accounts.presentation.payment.CustomerDialog
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import java.util.Date


@Composable
fun AddOrEditReceipt(
    navigateTo: (String) -> Unit = {},
    customerID: String? = null,
    receiptID: String? = null,
    viewModel: ReceiptViewModels = hiltViewModel()
) {
    if(!customerID.isNullOrBlank()){
        LaunchedEffect(customerID) {viewModel.setCustomerId(customerID)}
    }else if (!receiptID.isNullOrBlank()){
        LaunchedEffect(receiptID) { viewModel.setReceiptId(receiptID)}
    }
    val state by viewModel.individualState.collectAsState( ReceiptIndividualState() )
    when (state.loading) {
        true -> LoadingScreen()
        false -> AddReceiptImpl(
            //receipt = state.transaction,
            initialCustomer = state.customer,
            customers = state.customers,
            initialReceipt = state.transaction,
            matter = state.matter,
            navigateTo = navigateTo,
            updateReceipt = viewModel::updateReceipt
        )
    }
}


@Composable
private fun AddReceiptImpl(
    //receipt: Receipt,
    initialCustomer: Customer,
    customers: List<Customer>,
    initialReceipt: Receipt,
    matter: Matter,
    navigateTo: (String) -> Unit,
    updateReceipt: (Receipt) -> Unit,

    ) {
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { }
    val title = "Add or Edit Receipt"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) {
        AddReceiptContent(
            initialCustomer,
            customers,
            initialReceipt,
            matter,
            navigateTo,
            updateReceipt,
            Modifier.padding(it)
        )
    }
}


@Composable
fun AddReceiptContent(
    initialCustomer: Customer,
    customers: List<Customer>,
    initialReceipt: Receipt,
    initialMatter: Matter,
    navigateTo: (String) -> Unit,
    updateReceipt: (Receipt) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatterViewModels = hiltViewModel()
) {
    val TAG = "Add or Edit Receipt"
    var receiptTransient by remember {
        mutableStateOf(initialReceipt.copy(customerID = initialCustomer.documentID))
    }
    val state by viewModel.state.collectAsState(MatterListState())
    var matter by remember { mutableStateOf(initialMatter) }
    var selectedCustomer by remember { mutableStateOf(initialCustomer) }
    val onCustomerSelected: (Customer) -> Unit = {
        receiptTransient = receiptTransient.copy(customerID = it.documentID)
        selectedCustomer = it
    }
    var matterDialog by remember { mutableStateOf(false) }

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
        modifier = modifier.verticalScroll(rememberScrollState(0)).padding(horizontal = 10.dp),
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
        ItemSelect(
            items = state.matterList,
            isDialogShowing = matterDialog,
            onDismissRequest = {matterDialog = false},
            filterFunction = { matters, searchText ->
                matters.filter { it.title.contains(searchText, ignoreCase = true )}},
            cardText = {it.title},
            onItemSelected = {
                matter = it
                receiptTransient = receiptTransient.copy(matter = it.documentID)
            }
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

        ClickableTextField(
            value = matter.title,
            onValueChange = { receiptTransient = receiptTransient.copy(matter = it) },
            label = "Matter Title",
            modifier = Modifier.clickable { matterDialog = true }
        )

        Spacer(modifier = Modifier.padding(5.dp))
        Button(onClick = {
            if (receiptTransient.customerID.isBlank()) {
                Toast.makeText(context, "Select a Contact", Toast.LENGTH_SHORT).show()
                return@Button
            }
            Log.d(TAG, receiptTransient.toString())
            updateReceipt(receiptTransient)
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
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}

