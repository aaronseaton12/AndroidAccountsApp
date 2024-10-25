package com.aaronseaton.accounts

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.aaronseaton.accounts.presentation.about.AboutScreen
import com.aaronseaton.accounts.presentation.advancedstats.AdvancedStats
import com.aaronseaton.accounts.presentation.business.AddBusiness
import com.aaronseaton.accounts.presentation.business.EditBusiness
import com.aaronseaton.accounts.presentation.business.IndividualBusiness
import com.aaronseaton.accounts.presentation.business.ListOfBusinesses
import com.aaronseaton.accounts.presentation.customer.AddCustomer
import com.aaronseaton.accounts.presentation.customer.EditCustomer
import com.aaronseaton.accounts.presentation.customer.IndividualCustomer
import com.aaronseaton.accounts.presentation.customer.ListOfCustomers
import com.aaronseaton.accounts.presentation.home.Home
import com.aaronseaton.accounts.presentation.login.LoginOrHomeScreen
import com.aaronseaton.accounts.presentation.matter.AddMatter
import com.aaronseaton.accounts.presentation.matter.IndividualMatter
import com.aaronseaton.accounts.presentation.matter.ListOfMatters
import com.aaronseaton.accounts.presentation.payment.AddOrEditPayment
import com.aaronseaton.accounts.presentation.payment.IndividualPayment
import com.aaronseaton.accounts.presentation.payment.ListOfPayments
import com.aaronseaton.accounts.presentation.receipt.AddOrEditReceipt
import com.aaronseaton.accounts.presentation.receipt.IndividualReceipt
import com.aaronseaton.accounts.presentation.receipt.ListOfReceipts
import com.aaronseaton.accounts.presentation.task.AddTask
import com.aaronseaton.accounts.presentation.task.ListOfTasks
import com.aaronseaton.accounts.presentation.task.IndividualTask
import com.aaronseaton.accounts.presentation.user.EditUser
import com.aaronseaton.accounts.presentation.user.IndividualUser
import com.aaronseaton.accounts.presentation.transaction.TransactionScreen
import com.aaronseaton.accounts.util.Routes

private const val TAG = "NavGraph"

@ExperimentalGraphicsApi
@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    Log.d(TAG, "SetupNavGraph")
    val route = "accounts://routes"
    val navigateToPopUp: (String) -> Unit = {
        navController.navigate(it)
    }
    val navigateTo: (String) -> Unit = { navController.navigate(it) }
    val popBackStack: () -> Unit = { navController.popBackStack() }


    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(route = Routes.HOME) {
            Log.d(TAG, Routes.HOME)
            LoginOrHomeScreen(
                navigateTo = navigateToPopUp
            )
        }
        composable(route = Routes.REAL_HOME) {
            Log.d(TAG, Routes.REAL_HOME)
            Home(navigateTo = navigateToPopUp, signOut = {})
        }
        composable(Routes.ABOUT_SCREEN) {
            AboutScreen(navigateTo)
        }

        composable(Routes.ADVANCED_STATS) {
            AdvancedStats(
                navigateTo = navigateTo,
            )
        }
        composable(Routes.CUSTOMER_LIST) {
            ListOfCustomers(
                navigateTo = navigateToPopUp,
            )
        }
        composable(Routes.ADD_CUSTOMER) {
            AddCustomer(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
            )
        }
        composable(
            route = Routes.INDIVIDUAL_CUSTOMER + "/{customerID}",
            arguments = listOf(navArgument("customerID") {
                type = NavType.StringType
                nullable = false
            }),
        ) {

            IndividualCustomer(
                customerID = (it.arguments?.getString("customerID"))!!,
                navigateTo = navigateTo,
            )
        }
        composable(
            route = Routes.EDIT_CUSTOMER + "/{customerID}",
            arguments = listOf(navArgument("customerID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            EditCustomer(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
                customerID = (it.arguments?.getString("customerID"))!!,
            )
        }

        composable(
            route = Routes.PAYMENT_LIST, deepLinks = listOf(navDeepLink {
                uriPattern = "$route/payment_list"
            })
        ) {
            ListOfPayments(
                navigateTo = navigateToPopUp,
            )
        }
        composable(
            route = Routes.ADD_PAYMENT + "/{customerID}",
            arguments = listOf(navArgument("customerID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            AddOrEditPayment(
                customerID = (it.arguments?.getString("customerID")),
                navigateTo = navigateTo,
            )
        }
        composable(
            route = Routes.INDIVIDUAL_PAYMENT + "/{paymentID}",
            arguments = listOf(navArgument("paymentID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            IndividualPayment(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
                paymentID = (it.arguments?.getString("paymentID"))!!,
            )
        }
        composable(
            route = Routes.EDIT_PAYMENT + "/{paymentID}",
            arguments = listOf(navArgument("paymentID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            AddOrEditPayment(
                navigateTo = navigateTo,
                paymentID = (it.arguments?.getString("paymentID"))!!,
            )
        }
        composable(Routes.RECEIPT_LIST) {
            ListOfReceipts(
                navigateTo = navigateToPopUp,
            )
        }
        composable(
            route = Routes.ADD_RECEIPT + "/{customerID}",
            arguments = listOf(navArgument("customerID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            AddOrEditReceipt(
                navigateTo = navigateTo,
                customerID = (it.arguments?.getString("customerID")),
            )
        }
        composable(
            route = Routes.INDIVIDUAL_RECEIPT + "/{receiptID}", listOf(navArgument("receiptID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            IndividualReceipt(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
                receiptID = (it.arguments?.getString("receiptID"))!!,
            )
        }
        composable(
            route = Routes.EDIT_RECEIPT + "/{receiptID}", listOf(navArgument("receiptID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            AddOrEditReceipt(
                navigateTo = navigateTo,
                receiptID = (it.arguments?.getString("receiptID"))!!,
            )
        }
        composable(
            route = Routes.BUSINESS_LIST
        ) {
            ListOfBusinesses(
                navigateTo = navigateToPopUp, popBackStack = popBackStack
            )
        }
        composable(
            route = Routes.ADD_BUSINESS
        ) {
            AddBusiness(
                navigateTo = navigateTo, popBackStack = popBackStack
            )
        }
        composable(
            route = Routes.INDIVIDUAL_BUSINESS + "/" + "{businessID}",
            arguments = listOf(navArgument("businessID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            IndividualBusiness(
                businessID = (it.arguments?.getString("businessID"))!!, navigateTo = navigateTo
            )
        }
        composable(
            route = Routes.EDIT_BUSINESS + "/" + "{businessID}",
            arguments = listOf(navArgument("businessID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            EditBusiness(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
                businessID = (it.arguments?.getString("businessID"))!!
            )
        }
        composable(
            route = Routes.INDIVIDUAL_USER + "/" + "{userID}",
            arguments = listOf(navArgument("userID") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            IndividualUser(
                navigateTo = navigateTo, userID = (it.arguments?.getString("userID"))
            )
        }
        composable(
            route = Routes.EDIT_USER + "/" + "{userID}", arguments = listOf(navArgument("userID") {
                type = NavType.StringType
                nullable = false
            })
        ) {
            EditUser(
                navigateTo = navigateTo,
                popBackStack = popBackStack,
                userID = (it.arguments?.getString("userID"))!!
            )
        }
        composable(
            route = Routes.INDIVIDUAL_TASK + "/" + "{taskID}", arguments = listOf(
                navArgument("taskID") {
                    type = NavType.StringType
                    nullable = false
                },
            ), deepLinks = listOf(navDeepLink {
                this.uriPattern = "$route/task/{taskID}"
            })
        ) {
            IndividualTask(
                navigateTo = navigateTo,
                taskID = (it.arguments?.getString("taskID"))!!,
            )
        }
        composable(
            route = Routes.ADD_TASK
        ) {
            AddTask(
                navigateTo = navigateTo,
                popBackStack = popBackStack
            )
        }
        composable(
            route = Routes.TASK_LIST, deepLinks = listOf(navDeepLink {
                uriPattern = "$route/task_list"
            })
        ) {
            Log.d(TAG, Routes.TASK_LIST)
            ListOfTasks(
                navigateTo = navigateToPopUp
            )
        }

        composable(
            route = Routes.INDIVIDUAL_MATTER + "/" + "{matterID}", arguments = listOf(
                navArgument("matterID") {
                    type = NavType.StringType
                    nullable = false
                },
            ), deepLinks = listOf(navDeepLink {
                this.uriPattern = "$route/matter/{matterID}"
            })
        ) {
            IndividualMatter(
                navigateTo = navigateTo,
                matterID = (it.arguments?.getString("matterID"))!!,
            )
        }
        composable(
            route = Routes.ADD_MATTER
        ) {
            AddMatter(
                navigateTo = navigateTo, popBackStack = popBackStack
            )
        }
        composable(
            route = Routes.MATTER_LIST, deepLinks = listOf(navDeepLink {
                uriPattern = "$route/matter_list"
            })
        ) {
            Log.d(TAG, Routes.MATTER_LIST)
            ListOfMatters(
                navigateTo = navigateToPopUp
            )
        }
        composable(
            route = Routes.TRANSACTION_SCREEN) {
            Log.d(TAG, Routes.MATTER_LIST)
            TransactionScreen(
                navigateTo = navigateTo
            )
        }
    }
}