package com.aaronseaton.accounts.presentation.customer

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
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
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField

@Composable
fun AddCustomer(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    viewModel: IndividualCustomerViewModel = hiltViewModel()
) {
    AddCustomerImpl(viewModel::insertCustomer, navigateTo, popBackStack)
}


@Composable
fun AddCustomerImpl(
    insertCustomer: (Customer) -> Unit,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit = {},
) {
    val title = "Add Contact"
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val rightIcon = Icons.Default.Done
    var customer by remember { mutableStateOf(Customer()) }
    fun Customer.isBlank(): Boolean {
        return (this.firstName.isBlank() || this.lastName.isBlank())
    }

    fun saveCustomer(customer: Customer, context: Context) {
        if (customer.isBlank()) {
            Toast.makeText(context, "Enter Last and First Name", Toast.LENGTH_SHORT).show()
            return
        }
        customer.trimAllFields()
        insertCustomer(customer)
        Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show()
    }

    val onChangeCustomerInfo: (Customer) -> Unit = { customer = it }
    val context = LocalContext.current

    val description = "Save"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon,
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            saveCustomer(customer, context)
                            navigateTo(Routes.CUSTOMER_LIST)
                        },
                        content = {
                            Text("Save")
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        AddCustomerScaffold(customer, onChangeCustomerInfo, Modifier.padding(paddingValues))
    }
}

@Composable
fun AddCustomerScaffold(
    customer: Customer,
    onChangeCustomerInfo: (Customer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState(0))
    ) {
        EditOrAddTextField(customer.firstName, "First Name") {
            onChangeCustomerInfo(customer.copy(firstName = it))
        }
        EditOrAddTextField(customer.middleName, "Middle Name") {
            onChangeCustomerInfo(customer.copy(middleName = it))
        }
        EditOrAddTextField(customer.lastName, "Last Name") {
            onChangeCustomerInfo(customer.copy(lastName = it))
        }
        EditOrAddTextField(customer.emailAddress, "Email") {
            onChangeCustomerInfo(customer.copy(emailAddress = it))
        }
        EditOrAddTextField(customer.address.addressLine1, "Address Line 1") {
            onChangeCustomerInfo(customer.copy(address = customer.address.copy(addressLine1 = it)))
        }
        EditOrAddTextField(customer.address.addressLine2, "Address Line 2") {
            onChangeCustomerInfo(customer.copy(address = customer.address.copy(addressLine2 = it)))
        }
        EditOrAddTextField(customer.address.city, "City") {
            onChangeCustomerInfo(customer.copy(address = customer.address.copy(city = it)))
        }
        EditOrAddTextField(customer.address.country, "Country") {
            onChangeCustomerInfo(customer.copy(address = customer.address.copy(country = it)))
        }
        EditOrAddTextField(customer.phoneNumber.cellNumber, "Cell Number") {
            onChangeCustomerInfo(customer.copy(phoneNumber = customer.phoneNumber.copy(cellNumber = it)))
        }
        EditOrAddTextField(customer.phoneNumber.homeNumber, "Home Number") {
            onChangeCustomerInfo(customer.copy(phoneNumber = customer.phoneNumber.copy(homeNumber = it)))
        }
        EditOrAddTextField(customer.phoneNumber.workNumber, "Work Number") {
            onChangeCustomerInfo(customer.copy(phoneNumber = customer.phoneNumber.copy(workNumber = it)))
        }
        Spacer(modifier = Modifier.height(350.dp))
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun TestAddCustomerScreen() {
    AccountsTheme {
        AddCustomerImpl(insertCustomer = { "String" }, navigateTo = {})
    }
}
