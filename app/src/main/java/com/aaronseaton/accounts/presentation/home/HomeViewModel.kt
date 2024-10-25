package com.aaronseaton.accounts.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.RepoGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repoGroup: RepoGroup
): ViewModel() {
    private var selectedMonth by mutableIntStateOf(LocalDate.now().month.ordinal)
    private val year = MutableStateFlow(LocalDate.now().year)
    fun increaseYear(amount: Int = 1) = year.update { it + amount}
    fun decreaseYear(amount: Int = 1) = year.update { it - amount }

    val state = repoGroup.repos.combine(year) { repos, selectedYear ->
        coroutineScope {
            val business = async { repos.business.get(repos.accountUser.selectedBusiness) }
            val receipts = async { repos.receipt.list() }
            val payments = async { repos.payment.list() }
            val users = async { repos.user.list().filter { business.await().members.contains(it.documentID) } }
            val revenueForYear = receipts.await()
                .filter {it.date.year + 1900 == selectedYear  }
                .sumOf { it.amount }
            val expensesForYear = payments.await()
                .filter { it.date.year + 1900 == selectedYear  }
                .sumOf { it.amount }

            val incomeForYear = revenueForYear - expensesForYear
            val revenueByMonthAndYear = receipts.await()
                .filter { it.date.month == selectedMonth && it.date.year + 1900 == selectedYear }
                .sumOf { it.amount }
            val expenditureByMonthAndYear = payments.await()
                .filter { it.date.month == selectedMonth && it.date.year + 1900 == selectedYear }
                .sumOf { it.amount }
            val netIncomeByMonthAndYear =
                revenueByMonthAndYear - expenditureByMonthAndYear
            HomeState(
                repos.accountUser,
                users.await(),
                business.await(),
                revenueForYear,
                expensesForYear,
                incomeForYear,
                netIncomeByMonthAndYear,
                selectedYear = selectedYear,
                loading = false
            ).also { Log.d(TAG, "Emitting from HomeView" ) }
        }
    }

    companion object {
        val TAG = "Home ViewModel"
    }
}

data class HomeState(
    val accountUser: User = User(),
    val users: List<User> = listOf(User()),
    val business: Business = Business(),
    val revenueForYear: Double = 0.0,
    val expensesForYear: Double = 0.0,
    val incomeForYear: Double = 0.0,
    val incomeForMonth: Double = 0.0,
    val currentMonth: Month = LocalDate.now().month,
    val selectedYear: Int = LocalDate.now().year,
    val loading: Boolean = false,
)
