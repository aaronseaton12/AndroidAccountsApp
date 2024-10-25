package com.aaronseaton.accounts.presentation.user

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.*
import com.aaronseaton.accounts.presentation.customer.SearchAppBar

@Composable
fun EditUser(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    userID: String,
    viewModel: UserViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState(UserState(loading = true))
    when (state.loading) {
        true -> LoadingScreen()
        false ->
            EditUserImpl(
                state.user,
                state.usersBusinesses,
                state.selectedBusiness,
                viewModel::updateUser,
                viewModel::onChangeSelectedBusiness,
                navigateTo,
                popBackStack
            )
    }
}


@Composable
fun EditUserImpl(
    userState: User,
    businesses: List<Business>,
    business: Business,
    updateUser: (User) -> Unit,
    onChangeSelectedBusiness: (Business) -> Unit,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit
) {
    var user by remember { mutableStateOf(userState) }
    val description = "Back"
    val title = "Edit Contact"
    val leftIcon = Icons.Default.Person
    val onLeftIcon = { navigateTo(Routes.INDIVIDUAL_CUSTOMER) }
    val rightIcon = Icons.Default.Done

    val onRightButton = { updateUser(user.trimAllFields()) }
    val onEditTextChange: (User) -> Unit = { user = it }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(
                title, leftIcon, onLeftIcon,
                actions = {
                    Button(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        onClick = {
                            if (user.firstName.isBlank() || user.lastName.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Enter First & Last Name",
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

                }
            )
        },
        bottomBar = { AllBottomBar(navigateTo) },
    ) {
        EditUserContent(
            user,
            businesses,
            business,
            onChangeSelectedBusiness,
            onEditTextChange,
            Modifier.padding(it)
        )
    }
}



@Composable
private fun EditUserContent(
    user: User,
    businesses: List<Business>,
    business: Business,
    onBusinessSelected: (Business) -> Unit,
    onEditTextChange: (User) -> Unit,
    modifier: Modifier = Modifier
) {

    var isDialogShowing by remember { mutableStateOf(false) }
    val onDismissRequest = { isDialogShowing = false }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.verticalScroll(rememberScrollState(0))
    ) {

        BusinessDialog(
            isDialogShowing = isDialogShowing,
            onDismissRequest = onDismissRequest,
            businesses = businesses,
            onBusinessSelected = onBusinessSelected
        )

        EditOrAddTextField(name = user.firstName, label = "First Name") {
            onEditTextChange(user.copy(firstName = it))
        }
        EditOrAddTextField(name = user.middleName, label = "Middle Name") {
            onEditTextChange(user.copy(middleName = it))
        }
        EditOrAddTextField(name = user.lastName, label = "Last Name") {
            onEditTextChange(user.copy(lastName = it))
        }
        EditOrAddTextField(name = user.emailAddress, label = "Email") {
            onEditTextChange(user.copy(emailAddress = it))
        }
        EditOrAddTextField(name = user.address.addressLine1, label = "Address Line 1") {
            onEditTextChange(user.copy(address = user.address.copy(addressLine1 = it)))
        }
        EditOrAddTextField(name = user.address.addressLine2, label = "Address Line 2") {
            onEditTextChange(user.copy(address = user.address.copy(addressLine2 = it)))
        }
        EditOrAddTextField(name = user.address.city, label = "City") {
            onEditTextChange(user.copy(address = user.address.copy(city = it)))
        }
        EditOrAddTextField(name = user.address.country, label = "Country") {
            onEditTextChange(user.copy(address = user.address.copy(country = it)))
        }
        EditOrAddTextField(name = user.phoneNumber.cellNumber, label = "Cell Number") {
            onEditTextChange(user.copy(phoneNumber = user.phoneNumber.copy(cellNumber = it)))
        }
        EditOrAddTextField(name = user.phoneNumber.homeNumber, label = "Home Number") {
            onEditTextChange(user.copy(phoneNumber = user.phoneNumber.copy(homeNumber = it)))
        }
        EditOrAddTextField(name = user.phoneNumber.workNumber, label = "Work Number") {
            onEditTextChange(user.copy(phoneNumber = user.phoneNumber.copy(workNumber = it)))
        }
        ClickableTextField(
            value = business.name,
            onValueChange = {},
            label = "Selected Business",
            modifier = Modifier.clickable { isDialogShowing = true }
        )
        Spacer(modifier = Modifier.height(350.dp))
    }
}

@Composable
fun BusinessDialog(
    isDialogShowing: Boolean,
    onDismissRequest: () -> Unit,
    businesses: List<Business>,
    onBusinessSelected: (Business) -> Unit
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

                    val filteredBusiness = businesses
                        .filter {
                            it.name.contains(
                                searchAppText,
                                ignoreCase = true
                            )
                        }
                        .sortedBy {
                            it.name.first()
                        }
                    items(filteredBusiness) { business ->
                        BusinessCardPicker(
                            business,
                            onBusinessSelected,
                            onDismissRequest
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessCardPicker(
    business: Business,
    onBusinessSelected: (Business) -> Unit,
    onDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onBusinessSelected(business)
                onDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {

            Text(
                text = business.name,
                modifier = Modifier.padding(horizontal = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            Divider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}


//@Preview
//@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Preview("Large Screen", device = Devices.PIXEL_C)
//@Composable
//
//fun EditPreview() {
//    AccountsTheme {
//        EditUserImpl(
//            state = UserState(),
//            updateUser = {},
//            navigateTo = {},
//            onChangeSelectedBusiness = {}
//        )
//        {
//        }
//    }
//}