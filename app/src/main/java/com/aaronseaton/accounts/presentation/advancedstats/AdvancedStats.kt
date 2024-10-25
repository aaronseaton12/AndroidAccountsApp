package com.aaronseaton.accounts.presentation.advancedstats

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aaronseaton.accounts.presentation.components.AllBottomBar
import com.aaronseaton.accounts.presentation.components.AllTopAppBar
import com.aaronseaton.accounts.presentation.components.LoadingScreen
import com.aaronseaton.accounts.presentation.home.HomeCard
import com.aaronseaton.accounts.ui.theme.AccountsTheme
import com.aaronseaton.accounts.util.Routes

@Composable
fun AdvancedStats(
    navigateTo: (String) -> Unit = {},
    viewModel: AdvancedStatsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState(AdvancedStatsState())
    AdvancedStatsImpl(
        state,
        navigateTo
    )
}

@Composable
fun AdvancedStatsImpl(
    state: AdvancedStatsState,
    navigateTo: (String) -> Unit
) {
    val title = "Advanced Stats"
    val leftIcon = Icons.AutoMirrored.Filled.ArrowBack
    val onLeftIcon = { navigateTo(Routes.HOME) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AllTopAppBar(title, leftIcon, onLeftIcon) },
        bottomBar = { AllBottomBar(navigateTo) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        when (state.loading) {
            true -> LoadingScreen()
            false -> AdvancedStatsContent(
                state,
                Modifier.padding(padding)
            )

        }

    }
}

@Composable
fun AdvancedStatsContent(
    state: AdvancedStatsState,
    modifier: Modifier = Modifier
) {
    val lightness = if (isSystemInDarkTheme()) 0.2F else 0.85F
    val saturation = if (isSystemInDarkTheme()) 0.2F else 0.7F
    val monthHue = 221F
    val payingCustomerHue = 200F
    val vendorsPaidHue = 240F
    val space = 30.dp

    Column(modifier.verticalScroll(rememberScrollState(0))) {
        Column {
            var numberOfMonths by remember { mutableStateOf(3) }
            Text(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 3.dp),
                text = "Past Months",
                style = MaterialTheme.typography.titleMedium
            )
            state.netIncomeByYearMonth
                .reversed()
                .take(numberOfMonths)
                .forEach {
                    HomeCard(
                        description = "${it.first.month.name} ${it.first.year}",
                        amount = it.second,
                        color = Color.hsl(monthHue, saturation, lightness)
                    )
                }
            Text(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 3.dp)
                    .clickable { numberOfMonths++ },
                text = "See More.....",
                style = MaterialTheme.typography.titleMedium,
                color = Color(78, 129, 216, 255)
            )
        }
        Spacer(Modifier.height(space))
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 3.dp),
                text = "Best Paying Clients",
                style = MaterialTheme.typography.titleMedium
            )
            state.highestPayingCustomers.take(3).forEachIndexed { index, customer ->
                HomeCard(
                    description = "${index + 1}) ${customer.first.truncate(25, "...")}",
                    amount = customer.second,
                    color = Color.hsl(payingCustomerHue, saturation, lightness)
                )
            }
        }
        Spacer(Modifier.height(space))
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 3.dp),
                text = "Highest Paid Vendors",
                style = MaterialTheme.typography.titleMedium
            )
            state.highestPaidVendors.take(3).forEachIndexed { index, customer ->
                HomeCard(
                    description = "${index + 1}) ${customer.first.truncate(25, "...")}",
                    amount = customer.second,
                    color = Color.hsl(vendorsPaidHue, saturation, lightness)
                )
            }
        }
        Spacer(Modifier.height(space))
        Column {
            Text(
                modifier = Modifier.padding(horizontal = 30.dp, vertical = 3.dp),
                text = "Averages",
                style = MaterialTheme.typography.titleMedium
            )
            HomeCard(
                description = "Average Income Per Client",
                amount = state.averageRevenuePayment,
                color = Color.hsl(payingCustomerHue, saturation, lightness)
            )

            HomeCard(
                description = "Average Spent Per Vendor",
                amount = state.averageExpensePayment,
                color = Color.hsl(vendorsPaidHue, saturation, lightness)
            )
        }
    }
    Spacer(Modifier.height(space))
    Spacer(Modifier.height(space))
}


private fun String.truncate(limit: Int, stringEnd: String): String {
    return if (this.length > limit) {
        this.take(limit) + stringEnd
    } else this
}


@Preview
@Preview(name = "Night Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("Large Screen", device = Devices.PIXEL_C)
@Composable
private fun AdvancedStatsPreview() {
    AccountsTheme {
        AdvancedStatsImpl(
            state = AdvancedStatsState(),
            navigateTo = {}
        )
    }
}