package com.aaronseaton.accounts.presentation.business

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes

@Composable
fun AddBusiness(
    businessID: String? = null,
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    viewModel: BusinessViewModel = hiltViewModel()
    //navController: NavHostController
) {
    LaunchedEffect(businessID) {viewModel.setBusinessId(businessID)}
    val state by viewModel.individualState.collectAsState(IndividualBusinessState())

    when (state.loading) {
        true -> LoadingScreen()
        false -> AddBusinessImpl(
            state.accountUser,
            viewModel::insertBusiness,
            navigateTo,
            popBackStack
        )
    }


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddBusinessImpl(
    accountUser: User,
    insertBusiness: (Business) -> Unit,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit = {},
) {
    val title = "Add Business"
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { navigateTo(Routes.BUSINESS_LIST) }
    val rightIcon = Icons.Default.Done
    var business by remember {
        mutableStateOf(
            Business(
                members = mutableListOf(accountUser.documentID),
                owner = accountUser.selectedBusiness
            )
        )
    }
    val onRightButton = { insertBusiness(business.trimAllFields()) }
    val onChangeBusinessInfo: (Business) -> Unit = { business = it }
    val context = LocalContext.current

    val description = "Save"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(
                title, leftIcon, onLeftIcon,
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            if (business.name.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Enter Name",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                return@Button
                            }
                            onRightButton()
                            Toast.makeText(context, "Business Added", Toast.LENGTH_SHORT).show()
                            popBackStack()
                        }) {
                        Text("Save")
                    }
                })
        }
    ) { paddingValues ->
        AddBusinessScaffold(
            business,
            onChangeBusinessInfo,
            Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AddBusinessScaffold(
    business: Business,
    onChangeBusinessInfo: (Business) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 10.dp).verticalScroll(rememberScrollState(0))
    ) {
        EditOrAddTextField(name = business.name, label = "Business Name") {
            onChangeBusinessInfo(business.copy(name = it))
        }

        EditOrAddTextField(name = business.emailAddress, label = "Business Email") {
            onChangeBusinessInfo(business.copy(emailAddress = it))
        }
        EditOrAddTextField(name = business.address.addressLine1, label = "Address Line 1") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(addressLine1 = it)))
        }
        EditOrAddTextField(name = business.address.addressLine2, label = "Address Line 2") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(addressLine2 = it)))
        }
        EditOrAddTextField(name = business.address.city, label = "City") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(city = it)))
        }
        EditOrAddTextField(name = business.address.country, label = "Country") {
            onChangeBusinessInfo(business.copy(address = business.address.copy(country = it)))
        }
        EditOrAddTextField(name = business.phoneNumber.cellNumber, label = "Cell Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(cellNumber = it)))
        }
        EditOrAddTextField(name = business.phoneNumber.homeNumber, label = "Home Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(homeNumber = it)))
        }
        EditOrAddTextField(name = business.phoneNumber.workNumber, label = "Work Number") {
            onChangeBusinessInfo(business.copy(phoneNumber = business.phoneNumber.copy(workNumber = it)))
        }
        Spacer(modifier = Modifier.height(350.dp))
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun TestAddBusinessScreen() {
    AccountsTheme {
        AddBusinessImpl(accountUser = User(), insertBusiness = {}, navigateTo = {})
    }
}
