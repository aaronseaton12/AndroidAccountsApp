package com.aaronseaton.accounts.presentation.business

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.util.Routes
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen

@Composable
fun ListOfBusinesses(
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
    viewModel: BusinessViewModel = hiltViewModel()
) {
    val state by viewModel.businessListState.collectAsState()
    ListOfBusinessImpl(state, navigateTo, popBackStack)
}

@Composable
fun ListOfBusinessImpl(
    state: BusinessListState,
    navigateTo: (String) -> Unit = {},
    popBackStack: () -> Unit = {},
) {
    val title = "Businesses"
    val leftIcon = Icons.Default.ArrowBack
    val onLeftIcon = {}
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AllTopAppBar(title, leftIcon, onLeftIcon, actions = {
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
        bottomBar = {
            AllBottomBar(
                navigateTo = navigateTo
            )
        }
    ) {
        when (state.loading) {
            true -> LoadingScreen()
            false -> ListOfBusinessesContent(
                state.businesses,
                navigateTo,
                popBackStack,
                Modifier.padding(it)
            )
        }
    }
}

@Composable
fun ListOfBusinessesContent(
    businesses: List<Business>,
    navigateTo: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 150.dp),
        modifier = modifier
    ) {

        items(businesses) { business ->
            BusinessCard(
                business,
                navigateTo,
                popBackStack
            )
        }
        item {
            Button(onClick = { navigateTo(Routes.ADD_BUSINESS) }) {
                Text("Add Business")
            }
        }

//        val entity = "Payments"
//        item {
//            Button(onClick = addEntities ) {
//                Text("Transfer $entity")
//            }
//        }
    }
}

