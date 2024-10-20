package com.aaronseaton.accounts.presentation.home

import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.presentation.business.BusinessUsers
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes
import java.text.DecimalFormat

private const val TAG = "HomeScreen"

@Composable
fun Home(
    navigateTo: (String) -> Unit = {},
    signOut: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    Log.d(TAG, "Home()")
    val state by homeViewModel.homeState.collectAsState ()

    HomeScreenImpl(
        state,
        navigateTo,
        signOut,

        homeViewModel::increaseYear,
        homeViewModel::decreaseYear
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenImpl(
    state: HomeState,
    navigateTo: (String) -> Unit = {},
    signOut: () -> Unit = {},
    increaseYear: (Int) -> Unit,
    decreaseYear: (Int) -> Unit,
) {
    Log.d(TAG, "HomeScreenImpl()")
    val context = LocalContext.current
    val onPressed = { Toast.makeText(context, "Already Home", Toast.LENGTH_SHORT).show() }
    val icon = Icons.Filled.Home
    val description = "Home"
    val title = state.business.name
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Surface(shadowElevation = 5.dp) {
                CenterAlignedTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onPressed) {
                            Icon(icon, description)
                        }
                    },
                    actions = {
                        IconButton({ expanded = true }) {
                            Icon(Icons.Default.MoreVert, "Menu")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Account") },
                                onClick = { navigateTo(Routes.INDIVIDUAL_USER + "/" + state.accountUser.documentID) }
                            )
                            DropdownMenuItem(
                                text = { Text("Business") },
                                onClick = { navigateTo(Routes.BUSINESS_LIST) }
                            )
                            DropdownMenuItem(
                                text = { Text("Sign Out") },
                                onClick = signOut
                            )
                            DropdownMenuItem(
                                text = { Text("About") },
                                onClick = { navigateTo(Routes.ABOUT_SCREEN) }
                            )
                        }
                    },
                )
            }
        },
        bottomBar = { AllBottomBar(navigateTo, Routes.REAL_HOME) },
        //containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { padding ->
        when (state.loading) {
            true -> LoadingScreen()
            false -> HomeScreenContent(
                state,
                navigateTo,
                Modifier.padding(padding),
                increaseYear,
                decreaseYear,
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeState,
    navigateTo: (String) -> Unit,
    modifier: Modifier,
    increaseYear: (Int) -> Unit,
    decreaseYear: (Int) -> Unit,
) {
    Log.d(TAG, "HomeScreenContent()")
    Surface(tonalElevation = 0.dp) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            BusinessUsers(users = state.users, navigateTo = navigateTo)
            Text(
                text = (stringResource(R.string.home_welcome) + " " + state.accountUser.fullName),
                modifier = Modifier.padding(5.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            val homeCardColour = MaterialTheme.colorScheme.surface //Color(160, 220, 255, 250)
            HomeCard(
                description = "Total Revenue",
                amount = state.revenueForYear,
                color = homeCardColour,
                onClick = { navigateTo(Routes.RECEIPT_LIST) }
            )
            HomeCard(
                description = "Total Expenses",
                amount = state.expensesForYear,
                color = homeCardColour,
                onClick = { navigateTo(Routes.PAYMENT_LIST) }
            )
            HomeCard(
                description = "Net Income",
                amount = state.incomeForYear,
                color = homeCardColour
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    "See more",
                    Modifier
                        .padding(end = 10.dp)
                        .clickable { decreaseYear(1) }
                )
                Text("${state.selectedYear}", fontWeight = FontWeight.Bold)
                Icon(
                    Icons.Default.ArrowForward,
                    "See more",
                    Modifier
                        .padding(end = 10.dp)
                        .clickable { increaseYear(1) }
                )
            }

            Spacer(
                modifier = Modifier.height(50.dp)
            )
            HomeCard(
                description = "${state.currentMonth}'s Income",
                amount = state.incomeForMonth,
                color = homeCardColour
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .clickable { navigateTo(Routes.ADVANCED_STATS) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                color = homeCardColour,
                contentColor = MaterialTheme.colorScheme.onSurface,
                //border = BorderStroke(
                //    Dp.Hairline,
                //    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                //),
                tonalElevation = 2.dp,
                //shadowElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "See Advanced Stats",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                    Icon(
                        Icons.Default.ArrowForward,
                        "See more",
                        Modifier.padding(end = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    description: String,
    amount: Double,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit = {}
) {
    val decimalFormat = DecimalFormat("#,###,##0.00")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
        color = color,
        contentColor = MaterialTheme.colorScheme.onSurface,
        //border = BorderStroke(Dp.Hairline, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
        tonalElevation = 1.dp,
        //shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                description,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            Text(
                "$${decimalFormat.format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp)
            )
        }
    }
}

@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
fun TestHomeScreen() {
    AccountsTheme {
        HomeScreenImpl(
            HomeState(

            ),
            increaseYear = {},
            decreaseYear = {}
        )
    }
}
