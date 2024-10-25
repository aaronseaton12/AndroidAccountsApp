package com.aaronseaton.accounts.presentation.matter

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.aaronseaton.accounts.domain.model.MatterType
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.ClickableTextField
import com.aaronseaton.accounts.presentation.components.EditOrAddTextField
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.payment.CustomerDialog
import com.aaronseaton.accounts.util.Routes

@Composable
fun AddMatter(
    matterID: String? = null,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: MatterViewModels = hiltViewModel()
) {
    LaunchedEffect(matterID) {viewModel.setMatterId(matterID)}
    val state by viewModel.individualState.collectAsState(IndividualMatterState())

    when (state.loading) {
        true -> LoadingScreen()
        false -> AddMattersImpl(
            state,
            viewModel::insertMatter,
            navigateTo
        )
    }
}

@Composable
fun AddMattersImpl(
    state: IndividualMatterState,
    insertMatter: (Matter) -> Unit,
    navigateTo: (String) -> Unit
) {
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { navigateTo(Routes.MATTER_LIST) }
    val title = "Add or Edit Matter"
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
    ) { padding ->
        MatterInputContent(
            state,
            navigateTo,
            insertMatter,
            Modifier.padding(padding)
        )
    }
}

@Composable
fun MatterInputContent(
    state: IndividualMatterState,
    navigateTo: (String) -> Unit,
    insertMatter: (Matter) -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        var matter by remember { mutableStateOf(state.matter) }
        var selectedCustomer by remember { mutableStateOf(state.customer) }
        val onCustomerSelected = { customer: Customer ->
            matter = matter.copy(customerID = customer.customerID)
            selectedCustomer = customer
        }
        var isDialogShowing by remember { mutableStateOf(false) }
        var isMatterDialogShowing by remember { mutableStateOf(false) }
        val onMatterDismissRequest = {isMatterDialogShowing = false}
        val onDismissRequest = { isDialogShowing = false }
        val onMatterMethodSelected = {type: String -> matter = matter.copy(type = type)}

        CustomerDialog(
            isDialogShowing = isDialogShowing,
            onDismissRequest = onDismissRequest,
            customers = state.customers,
            onCustomerSelected = onCustomerSelected
        )
        MatterTypeDialog(
            isMatterDialogShowing = isMatterDialogShowing,
            onMatterDismissRequest = onMatterDismissRequest,
            matterType = matter.type,
            onMatterMethodSelected = onMatterMethodSelected
        )

        EditOrAddTextField(
            name = matter.title,
            label = "Title"
        ) { matter = matter.copy(title = it) }
        EditOrAddTextField(
            name = matter.description,
            label = "Description"
        ) { matter = matter.copy(description = it) }
        ClickableTextField(
            value = selectedCustomer.fullName(),
            onValueChange = {},
            label = "Customer Name",
            modifier = Modifier.clickable { isDialogShowing = true }
        )
        ClickableTextField(
            value = matter.type,
            onValueChange = { matter = matter.copy(type = it) },
            label = "Matter Type",
            modifier = Modifier.clickable { isMatterDialogShowing = true }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            if (selectedCustomer.documentID.isBlank()) {
                Toast.makeText(context, "Select a Customer", Toast.LENGTH_SHORT).show()
                return@Button
            }
            insertMatter(matter.copy(
                customerID = selectedCustomer.documentID,
                createdBy = state.accountUser.documentID,
                responsibleAttorney = state.accountUser.documentID
            ))
            Toast.makeText(context, "${matter.javaClass.simpleName} Added", Toast.LENGTH_SHORT)
                .show()
            navigateTo(Routes.MATTER_LIST)
        }
        ) {
            Text(text = "Add Matter")
        }
    }
}

@Composable
fun MatterTypeDialog(
    isMatterDialogShowing: Boolean,
    onMatterDismissRequest: () -> Unit,
    matterType: String?,
    onMatterMethodSelected: (String) -> Unit
) {
    if (isMatterDialogShowing)
        Dialog(onMatterDismissRequest) {
            Column {
                LazyColumn(Modifier.padding(bottom = 60.dp)) {

                    val items = MatterType.values()
                    items(items) { item ->
                        MatterTypePicker(
                            item.type,
                            onMatterMethodSelected,
                            onMatterDismissRequest
                        )
                    }
                }
            }
        }

}


@Composable
fun MatterTypePicker(
    type: String,
    onMatterMethodSelected: (String) -> Unit,
    onMatterDismissRequest: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMatterMethodSelected(type)
                onMatterDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {

            Text(
                text = type,
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}