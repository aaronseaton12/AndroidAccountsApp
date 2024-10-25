package com.aaronseaton.accounts.presentation.matter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.MatterSorting
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.customer.CustomerViewModels
import com.aaronseaton.accounts.presentation.customer.IndividualCustomerState
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.util.Util.Companion.paymentFormat

@Composable
fun ListOfMatters(
    navigateTo: (String) -> Unit,
    viewModel: MatterViewModels = hiltViewModel()
) {
    val state by viewModel.state.collectAsState(MatterListState())
    ListOfMattersImpl(
        state,
        viewModel::changeSorting,
        navigateTo,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListOfMattersImpl(
    state: MatterListState,
    changeSorting: (MatterSorting) -> Unit,
    navigateTo: (String) -> Unit)
{
    val icon = Icons.AutoMirrored.Filled.ArrowBack
    val onHomeButtonClicked = { navigateTo(Routes.HOME) }
    val onFabPressed = { navigateTo(Routes.ADD_MATTER ) }
    val title = "List of Matters"
    val description = "Back"
    var expanded by remember { mutableStateOf(false) }
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = onHomeButtonClicked) {
                            Icon(icon, description)
                        }
                    },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Search"
                            )
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Sort By Date") },
                                onClick = { changeSorting(MatterSorting.BY_DATE_CREATED) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Customer") },
                                onClick = { changeSorting(MatterSorting.BY_CUSTOMER_FIRSTNAME) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort By Value") },
                                onClick = { changeSorting(MatterSorting.BY_VALUE) }
                            )
                        }
                    }
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.MATTER_LIST) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Add Matter") },
                icon = { Icon(Icons.Default.Add, "Add Matter") },
                onClick = onFabPressed
            )
        },

    ){paddingValues ->
        when(state.loading) {
            true -> LoadingScreen()
            false -> MatterOutputArea(
                navigateTo,
                state.matterList,
                Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun MatterOutputArea(navigateTo: (String) -> Unit, matterList: List<Matter>, modifier: Modifier) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 150.dp),
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly ) {
        val sortedMatterList = matterList.sortedBy { it.number }
        items(sortedMatterList) { matter ->
            MatterCard(
                matter = matter ,
                navigateTo = navigateTo,
            )
        }
    }
}

@Composable
fun MatterCard(
    matter: Matter,
    navigateTo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CustomerViewModels = hiltViewModel()
) {
    val state by viewModel.individualCustomerState(matter.customerID).collectAsState(IndividualCustomerState())
    val labelStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
    )
    val valueStyle = MaterialTheme.typography.titleMedium
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 3.dp)
            .clickable { navigateTo(Routes.INDIVIDUAL_MATTER + "/" + matter.documentID) },
        contentAlignment = Alignment.Center
    ){
        Surface (
            Modifier
                .fillMaxWidth(),
            tonalElevation = 0.25.dp,
        ) {

            Row {
                Column(Modifier.padding(vertical = 10.dp, horizontal = 10.dp).weight(1f)) {
                    Text(
                        text = "Matter No. ${paymentFormat.format(matter.number)}",
                        style = valueStyle.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = matter.title,
                        style = labelStyle.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    val customer = when(state.loading){
                        true -> "Loading..."
                        else -> state.customer.fullName()
                    }
                    Text(
                        text = customer,
                        style = labelStyle.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    )
                }
                Column (Modifier.padding(vertical = 10.dp, horizontal = 10.dp).weight(1f))  {
                    Text(text = matter.description, style = labelStyle)
                }
            }

        }
    }
}



