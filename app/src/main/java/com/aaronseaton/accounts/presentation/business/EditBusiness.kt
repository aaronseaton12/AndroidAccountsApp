package com.aaronseaton.accounts.presentation.business

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
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.LoadingScreen

@Composable
fun EditBusiness(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    businessID: String,
    viewModel: BusinessViewModel = hiltViewModel()
) {
    viewModel.getBusiness(businessID)
    val state by viewModel.individualBusinessState.collectAsState()

    when (state.loading) {
        true -> LoadingScreen()
        false -> EditBusinessImpl(
            state.business,
            viewModel::updateBusiness,
            navigateTo,
            popBackStack
        )
    }
}


@Composable
fun EditBusinessImpl(
    businessToEdit: Business,
    updateBusiness: (Business) -> Unit,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit
) {
    val description = "Back"
    val title = "Edit Business"
    val leftIcon = Icons.Default.ArrowBack
    val rightIcon = Icons.Default.Done
    var business by remember { mutableStateOf(businessToEdit) }
    val onRightButton = { updateBusiness(business.trimAllFields()) }
    val onChangeBusinessInfo: (Business) -> Unit = { business = it }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(
                title, leftIcon, popBackStack,
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            if (business.name.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Enter Name",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            onRightButton()
                            Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
                            popBackStack()
                        }) {
                        Text("Save")
                    }

                })
        },
        bottomBar = { AllBottomBar(navigateTo) },
    ) {
        EditBusinessContent(
            business,
            onChangeBusinessInfo,
            Modifier.padding(it)
        )
    }
}


@Composable
private fun EditBusinessContent(
    business: Business,
    onChangeBusinessInfo: (Business) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState(0))
    ) {
        EditOrAddTextField(business.name, "Name") {
            onChangeBusinessInfo(business.copy(name = it))
        }
        EditOrAddTextField(business.emailAddress, "Email") {
            onChangeBusinessInfo(business.copy(emailAddress = it))
        }
        EditOrAddTextField(business.address.addressLine1, "Address Line 1") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(addressLine1 = it)))
        }
        EditOrAddTextField(business.address.addressLine2, "Address Line 2") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(addressLine2 = it)))
        }
        EditOrAddTextField(business.address.city, "City") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(city = it)))
        }
        EditOrAddTextField(business.address.country, "Country") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(country = it)))
        }
        EditOrAddTextField(business.phoneNumber.cellNumber, "Cell Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(cellNumber = it)))
        }
        EditOrAddTextField(business.phoneNumber.homeNumber, "Home Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(homeNumber = it)))
        }
        EditOrAddTextField(business.phoneNumber.workNumber, "Work Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(workNumber = it)))
        }
        Spacer(modifier = Modifier.height(350.dp))
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable

fun EditPreview() {
    AccountsTheme {
        EditBusinessImpl(businessToEdit = Business(), updateBusiness = {}, navigateTo = {}) {
        }
    }
}