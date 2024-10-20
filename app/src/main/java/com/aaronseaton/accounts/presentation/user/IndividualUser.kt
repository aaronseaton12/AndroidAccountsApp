package com.aaronseaton.accounts.presentation.user

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AccountDivider
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen

private const val TAG = "User Screen"

@Composable
fun IndividualUser(
    navigateTo: (String) -> Unit = {},
    userID: String? = null,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val state by userViewModel.state.collectAsState()
    state.selectedBusiness.also { println("From UI: ${it.name}") }

    IndividualUserImpl(
        state,
        userViewModel :: onChangeSelectedBusiness,
        navigateTo
    )
}


@Composable
fun IndividualUserImpl(
    state: UserState,
    changeSelectedBusiness: (Business) -> Unit,
    navigateTo: (String) -> Unit = {}
) {
    val fabModifier = Modifier
        .width(160.dp)
        .padding(2.dp)
    //.sizeIn(130.dp, 50.dp, 200.dp, 100.dp)
    val title = ""
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val onAddBusiness = { navigateTo(Routes.ADD_BUSINESS + "/${state.user.documentID}") }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon, actions = {
                IconButton({ navigateTo(Routes.EDIT_USER + "/" + state.user.documentID) })
                {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                IconButton({ expanded = true }) {
                    Icon(Icons.Default.MoreVert, "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Account") },
                        onClick = { navigateTo(Routes.INDIVIDUAL_USER + "/" + "NOVALUE") }
                    )
                    DropdownMenuItem(
                        text = { Text("Business") },
                        onClick = { navigateTo(Routes.BUSINESS_LIST) }
                    )
                    DropdownMenuItem(
                        text = { Text("About") },
                        onClick = { navigateTo(Routes.ABOUT_SCREEN) }
                    )
                }
            })
        },
        bottomBar = { AllBottomBar(navigateTo) }
    ) {
        when (state.loading) {
            true -> LoadingScreen()
            false -> Column(Modifier.padding(it)) {
                UserInformation(state.user, navigateTo)
                Text(
                    text = "Businesses ${state.user.fullName} is a member of. \nPlease Select One",
                    modifier = Modifier.padding(horizontal = 20.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                )
                AccountDivider()
                UserBusinesses(
                    state.selectedBusiness,
                    state.usersBusinesses,
                    changeSelectedBusiness,
                    navigateTo
                )
            }
        }
    }
}

@Composable
fun UserInformation(
    user: User,
    navigateTo: (String) -> Unit = {}
) {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        //.clickable {navController.navigate("edit_payment/$paymentID")}
    ) {
        Row {
            Column(modifier = Modifier.padding(start = 20.dp)) {
                AsyncImage(
                    model = user.photoUrl,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .size(120.dp)
                        .border(
                            Dp.Hairline,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            MaterialTheme.shapes.medium
                        )
                )
                Spacer(modifier = Modifier.height(15.dp))

            }

            Column(modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = "${user.firstName} ${user.lastName}",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Left
                )
                Text(
                    text = user.phoneNumber.cellNumber.ifBlank { "Please add phone #" },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = user.emailAddress.ifBlank { "Please add email" },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "${user.address.addressLine1} " +
                            "\n${user.address.addressLine2}" +
                            "\n${user.address.city}".ifBlank { "Please add address" },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun UserBusinesses(
    selectedBusiness: Business,
    userBusinesses: List<Business>,
    changeSelectedBusiness: (Business) -> Unit,
    navigateTo: (String) -> Unit
) {
    Log.d(TAG, selectedBusiness.documentID)
    val selectedOption = selectedBusiness
    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
    Column(Modifier.selectableGroup()) {
        userBusinesses.forEach { business ->
            UserBusinessCard(
                business,
                changeSelectedBusiness,
                selectedOption,
                navigateTo
            )
        }
    }
}



@Composable
private fun UserBusinessCard(
    business: Business,
    changeSelectedBusiness: (Business) -> Unit,
    selectedOption: Business,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .heightIn()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (business == selectedOption),
            onClick = { changeSelectedBusiness(business) }// null recommended for accessibility with screenreaders
        )
        Column(
            Modifier
                .padding(12.dp)
                .clickable { navigateTo(Routes.INDIVIDUAL_BUSINESS + "/" + business.documentID) }

        ) {
            Text(
                text = business.name,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                textAlign = TextAlign.Left
            )
            Text(
                text = business.emailAddress.ifBlank { "Please add email" },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "${business.address}",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            Text(
                text = "${business.phoneNumber}",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left
            )
            //Spacer(modifier = Modifier.padding(vertical = 5.dp))
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun IndividualUserPreview() {
    AccountsTheme {
        IndividualUserImpl(
            UserState(
                user = User(),
                selectedBusiness = Business(),
                usersBusinesses = listOf(
                    Business(name = "Aaron Seaton, Attorney-at-Law"),
                    Business(name = "Madrigal Developments Limited"),
                    Business(name = "Project Runway and Runaway")
                ),
                loading = false
            ),
            changeSelectedBusiness = {},
            navigateTo = {},

            )
    }
}




