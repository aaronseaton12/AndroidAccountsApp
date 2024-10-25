package com.aaronseaton.accounts.presentation.payment

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.PaymentMethod
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.ClickableTextField
import com.aaronseaton.accounts.presentation.components.EditOrAddNumberField
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.ItemSelect
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.components.showDatePicker
import com.aaronseaton.accounts.presentation.customer.SearchAppBar
import com.aaronseaton.accounts.presentation.matter.MatterListState
import com.aaronseaton.accounts.presentation.matter.MatterViewModels
import com.aaronseaton.accounts.presentation.receipt.PaymentMethodDialog
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util
import com.aaronseaton.accounts.util.Util.Companion.dateFormatter
import com.aaronseaton.accounts.util.Util.Companion.isValidDoubleString
import java.util.Date

@Composable
fun AddOrEditPayment(
    customerID: String? = null,
    paymentID: String? = null,
    navigateTo: (String) -> Unit = {},
    viewModel: PaymentViewModels = hiltViewModel()
) {
    if(!customerID.isNullOrBlank()){
        LaunchedEffect(customerID) {viewModel.setCustomerId(customerID)}
    }else if (!paymentID.isNullOrBlank()){
        LaunchedEffect(paymentID) { viewModel.setPaymentId(paymentID)}
    }

    val state by viewModel.individualState.collectAsState(PaymentIndividualState())

    when (state.loading) {
        true -> LoadingScreen()
        false -> AddPaymentImpl(
            initialCustomer = state.customer,
            initialPayment = state.transaction,
            customers = state.customers,
            matter = state.matter,
            insertPayment = viewModel::updatePayment,
            navigateTo = navigateTo
        )
    }
}


@Composable
private fun AddPaymentImpl(
    initialCustomer: Customer,
    initialPayment: Payment,
    customers: List<Customer>,
    matter: Matter,
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
            initialPayment,
            customers,
            matter,
            navigateTo,
            insertPayment,
            Modifier.padding(padding)
        )
    }
}

@Composable
fun PaymentInputContent(
    initialCustomer: Customer,
    initialPayment: Payment,
    customers: List<Customer>,
    initialMatter: Matter,
    navigateTo: (String) -> Unit,
    updatePayment: (Payment) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatterViewModels = hiltViewModel()
) {
    val TAG = "Add or Edit Payment"
    val state by viewModel.state.collectAsState(MatterListState())
    var matter by remember { mutableStateOf(initialMatter) }
    var paymentTransient by remember {
        mutableStateOf(initialPayment.copy(
            customerID = initialCustomer.documentID,
            payMethod = if(initialPayment.payMethod.isNullOrBlank()) initialPayment.payMethod else PaymentMethod.CASH.type
        ))
    }
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
    var matterDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 10.dp),
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
        ItemSelect(
            items = state.matterList,
            isDialogShowing = matterDialog,
            onDismissRequest = {matterDialog = false},
            filterFunction = { matters, searchText ->
                matters.filter { it.title.contains(searchText, ignoreCase = true )}},
            cardText = {it.title},
            onItemSelected = {
                matter = it
                paymentTransient = paymentTransient.copy(matter = it.documentID)
            }
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

        ClickableTextField(
            value = matter.title,
            onValueChange = { paymentTransient = paymentTransient.copy(matter = it) },
            label = "Matter Title",
            modifier = Modifier.clickable { matterDialog = true }
        )

        Spacer(modifier = Modifier.padding(5.dp))
        Button(onClick = {
            if (paymentTransient.customerID.isBlank()) {
                Toast.makeText(context, "Select a Customer", Toast.LENGTH_SHORT).show()
                return@Button
            }
            Log.d(TAG, paymentTransient.toString())
            updatePayment(paymentTransient)
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
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}