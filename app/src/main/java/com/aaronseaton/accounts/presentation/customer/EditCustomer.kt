package com.aaronseaton.accounts.presentation.customer

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
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
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.LoadingScreen

@Composable
fun EditCustomer(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    customerID: String,
    viewModel: IndividualCustomerViewModel = hiltViewModel()
) {
    viewModel.getCustomer(customerID)
    val uiState by viewModel.individualCustomerState.collectAsState()
    when (uiState.loading) {
        true -> LoadingScreen()
        false ->
            EditCustomerImpl(
                uiState,
                viewModel::updateCustomer,
                navigateTo,
                popBackStack
            )
    }

}

@Composable
fun EditCustomerImpl(
    uiState: IndividualCustomerState,
    updateCustomer: (Customer) -> Unit,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit
) {
    val description = "Back"
    val title = "Edit Contact"
    val leftIcon = Icons.Default.Person
    val onLeftIcon = { navigateTo(Routes.INDIVIDUAL_CUSTOMER) }
    val rightIcon = Icons.Default.Done
    var customer by remember { mutableStateOf(uiState.customer) }
    val onRightButton = { updateCustomer(customer.trimAllFields()) }
    val onChangeCustomerInfo: (Customer) -> Unit = { customer = it }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon,
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            if (customer.firstName.isBlank() || customer.lastName.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Enter First & Last Name",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            onRightButton()
                            Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
                            navigateTo(Routes.CUSTOMER_LIST)
                    }) {
                        Text("Save")
                    }

                })
        },
        bottomBar = { AllBottomBar(navigateTo) },
    ) {
        EditCustomerContent(
            customer,
            onChangeCustomerInfo,
            Modifier.padding(it)
        )
    }
}

@Composable
private fun EditCustomerContent(
    customer: Customer,
    onChangeCustomerInfo: (Customer) -> Unit,
    modifier: Modifier = Modifier
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

//@Preview
//@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Preview("Large Screen", device = Devices.PIXEL_C)
//@Composable
//
//fun EditPreview() {
//    AccountsTheme {
//        EditCustomerImpl(customerToEdit = Customer(), updateCustomer = {}, navigateTo = {}) {
//        }
//    }
//}