package com.aaronseaton.accounts.presentation.business

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Response
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Constants
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.*
import kotlinx.coroutines.Job

@Composable
fun IndividualBusiness(
    businessID: String,
    navigateTo: (String) -> Unit = {},
    viewModel: BusinessViewModel = hiltViewModel()
) {
    viewModel.getBusiness(businessID)
    val state by viewModel.individualBusinessState.collectAsState()

    when (state.loading) {
        true -> LoadingScreen()
        false -> IndividualBusinessImpl(
            state.business,
            state.users,
            navigateTo,
            viewModel::updateBusiness,
            viewModel::addImageToStorage,
            viewModel::addImageToDatabase,
            viewModel.addImageToStorageResponse
        )
    }
}

@Composable
fun IndividualBusinessImpl(
    business: Business,
    users: List<User>,
    navigateTo: (String) -> Unit,
    updateBusiness: (Business) -> Unit,
    addImageToStorage: (Uri, String) -> Job,
    addImageToDatabase: (Business, Uri) -> Job,
    addImageToStorageResponse: Response<Uri>
) {
    var showThreeButtons by remember { mutableStateOf(false) }
    val fabText = if (showThreeButtons) "Close This" else "Add/Delete"
    val fabIcon = if (showThreeButtons) Icons.Default.Close else Icons.Default.Add
    val fabModifier = Modifier
        .width(160.dp)
        .padding(2.dp)
    //.sizeIn(130.dp, 50.dp, 200.dp, 100.dp)
    val title = ""
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = { navigateTo(Routes.CUSTOMER_LIST) }
    val onFabPressed = { showThreeButtons = !showThreeButtons }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val onAddMember = { showAddMemberDialog = true }
    val onRemoveMember = { showDeleteDialog = true }
    val onAddMemberDialogDismiss = { showAddMemberDialog = false }
    val onRemoveMemberDialogDismiss = { showDeleteDialog = false }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon, actions = {
                IconButton({ navigateTo(Routes.EDIT_BUSINESS + "/" + business.documentID) })
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
        bottomBar = { AllBottomBar(navigateTo) },
        floatingActionButton = {
            CompoundFAB(
                fabLabel = fabText,
                fabIcon = fabIcon,
                onTopFabPressed = onAddMember,
                onBottomFabPressed = onRemoveMember,
                topFabLabel = "Add Member",
                bottomFabLabel = "Remove Member"
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            AddMemberDialog(
                showAddMemberDialog,
                business,
                updateBusiness,
                onAddMemberDialogDismiss
            )
            RemoveMemberDialog(
                showDeleteDialog,
                business,
                updateBusiness,
                onRemoveMemberDialogDismiss
            )
            BusinessInformation(
                business,
                navigateTo,
                addImageToStorage,
                addImageToDatabase,
                addImageToStorageResponse
            )
            Text(
                text = "Members of This Business",
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            )
            BusinessUsers(
                users,
                navigateTo
            )
        }
    }
}

@Composable
fun RemoveMemberDialog(
    showDeleteDialog: Boolean,
    business: Business,
    updateBusiness: (Business) -> Unit,
    onRemoveMemberDialogDismiss: () -> Unit
) {
    if (showDeleteDialog)
        Dialog(onDismissRequest = onRemoveMemberDialogDismiss) {
            Surface(color = MaterialTheme.colorScheme.primary) {
                Surface(tonalElevation = 2.dp) {
                    Column(
                        modifier = Modifier.size(400.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(business.documentID)
                    }
                }
            }
        }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    showAddMemberDialog: Boolean,
    business: Business,
    updateBusiness: (Business) -> Unit,
    onAddMemberDialogDismiss: () -> Unit
) {
    var newMemberEmail by remember { mutableStateOf("") }
    AccountsDialog(
        showDialog = showAddMemberDialog,
        positiveButtonLabel = "Save",
        negativeButtonLabel = "Cancel",
        onNegativeButtonPressed = onAddMemberDialogDismiss,
        onPositiveButtonPressed = {
            val pendingMembers = business.pendingMembers
            if (pendingMembers.contains(newMemberEmail.trim())) return@AccountsDialog
            pendingMembers.add(newMemberEmail.trim())
            updateBusiness(business.copy(pendingMembers = pendingMembers))
            onAddMemberDialogDismiss()
        }

    ) {
        Column {
            Text("Email of Person To Add", modifier = Modifier.padding(10.dp))
            TextField(
                value = newMemberEmail,
                onValueChange = { newMemberEmail = it },
                modifier = Modifier.padding(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun BusinessInformation(
    business: Business,
    navigateTo: (String) -> Unit = {},
    addImageToStorage: (Uri, String) -> Job,
    addImageToDatabase: (Business, Uri) -> Job,
    addImageToStorageResponse: Response<Uri>
    //viewModel: BusinessViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
            imageUri?.let {
                addImageToStorage(imageUri, business.name)
            }
        }

    var businessUrl by remember { mutableStateOf(Uri.parse(business.logo)) }

    when (addImageToStorageResponse) {
        is Response.Loading -> {}
        is Response.Success -> addImageToStorageResponse.data?.let { downloadUrl ->
            businessUrl = downloadUrl
            LaunchedEffect(downloadUrl) {
                businessUrl = downloadUrl
                println("Success")
                addImageToDatabase(business, downloadUrl)
            }
        }
        is Response.Failure -> LaunchedEffect(Unit) {
            print(addImageToStorageResponse.e)
        }
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 1.dp,
        //.clickable {navController.navigate("edit_payment/$paymentID")}
    ) {
        businessUrl.let { println(it) }

        Row {
            Column(modifier = Modifier.padding(start = 15.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(businessUrl ?: R.drawable.business_icon)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholder = painterResource(id = R.drawable.business_icon),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .size(120.dp)
                        .border(
                            Dp.Hairline,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            MaterialTheme.shapes.medium
                        )
                        .clickable { galleryLauncher.launch(Constants.ALL_IMAGES) }
                )
                Spacer(modifier = Modifier.height(15.dp))

            }
            Column(modifier = Modifier.padding(5.dp)) {
                Text(
                    text = business.name,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    textAlign = TextAlign.Left
                )
                Text(
                    text = business.phoneNumber.cellNumber.ifBlank { "Please add phone #" },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = business.emailAddress.ifBlank { "Please add email" },
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left
                )
                Text(
                    text = "${business.address.addressLine1} " +
                            "\n${business.address.addressLine2}" +
                            "\n${business.address.city}".ifBlank { "Please add address" },
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
fun BusinessUsers(
    users: List<User>,
    navigateTo: (String) -> Unit
) {
    LazyRow {
        items(users) { user ->
            Column(Modifier.padding(15.dp)) {
                AsyncImage(
                    model = user.photoUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        //.padding(15.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .size(80.dp)
                        .border(
                            Dp.Hairline,
                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                            MaterialTheme.shapes.medium
                        )
                        .clickable { navigateTo(Routes.INDIVIDUAL_USER + "/" + user.documentID) }
                )
                Text(user.firstName)
            }
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun IndividualBusinessPreview() {
    AccountsTheme {
        IndividualBusinessImpl(
            business = Business(),
            users = listOf(User()),
            navigateTo = {},
            updateBusiness = { Job() },
            addImageToStorage = { _, _ -> Job() },
            addImageToDatabase = { _, _ -> Job() },
            addImageToStorageResponse = Response.Success(Uri.EMPTY)
        )
    }
}



