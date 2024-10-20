package com.aaronseaton.accounts.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.aaronseaton.accounts.R
import com.aaronseaton.accounts.util.Routes

@Composable
fun AllBottomBar(
    navigateTo: (String) -> Unit,
    route: String = ""
) {

    data class NavBarData(val label: String, val icon: Any, val route: String)

    val home = NavBarData("Home", Icons.Filled.Home, Routes.REAL_HOME)
    val clients = NavBarData("Clients", Icons.Filled.Person, Routes.CUSTOMER_LIST)
    val receipts = NavBarData("Income", R.drawable.money_icon, Routes.RECEIPT_LIST)
    val payments = NavBarData("Expenses", Icons.Filled.ShoppingCart, Routes.PAYMENT_LIST)
    val tasks = NavBarData("Tasks", R.drawable.event_icon, Routes.TASK_LIST)

    val items = listOf(home, clients, receipts, payments, tasks)

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = route == item.route,
                onClick = { navigateTo(item.route) },
                icon = {
                    when (item.icon) {
                        is ImageVector -> Icon(item.icon, contentDescription = null)
                        is Int -> Icon(painterResource(id = item.icon), contentDescription = null)
                    }
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = LocalContentColor.current.copy(alpha = 0.6f)
                )
            )
        }
    }
}