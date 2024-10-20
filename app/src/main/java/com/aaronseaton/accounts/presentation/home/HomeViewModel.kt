package com.aaronseaton.accounts.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import com.aaronseaton.accounts.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountUser: User,
    private val businessRepo: EntityRepo<Business>,
    private val receiptRepo: EntityRepo<Receipt>,
    private val paymentRepo: EntityRepo<Payment>,
    private val userRepo: EntityRepo<User>,
    private val accountUserRepo: UserRepository,
    //private val repository: FlowRepository,
    private val repo: ProfileImageRepository
): ViewModel() {

    private val _homeState = MutableStateFlow(HomeState(loading = true))
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    private var selectedMonth by mutableIntStateOf(LocalDate.now().month.ordinal)
    private var selectedYear by mutableIntStateOf(LocalDate.now().year)

    init {
        Log.d(TAG, "Initialization")
        Log.d(TAG, "Time Before")
        viewModelScope.launch {
            accountUserRepo.accountUser.collect{user->
                Log.d(TAG, "Current User: ${user?.fullName()}\nBusiness: ${user?.selectedBusiness}")
            }
        }
        viewModelScope.launch {
            updateHomeState()
        }
        _homeState.update { it.copy(loading = false) }
        Log.d(TAG, "Time After")
    }

    fun increaseYear(amount: Int = 1) {
        selectedYear += amount
        updateHomeState()
    }

    fun decreaseYear(amount: Int = 1) {
        selectedYear -= amount
        updateHomeState()
    }

    private fun updateHomeState() = viewModelScope.launch(Dispatchers.IO) {
        Log.d(TAG, "UpdateHomeState()")
        Log.d(TAG, "SetAccountUser()")
        val accountUser = accountUser
        val business = async { businessRepo.get(accountUser.selectedBusiness) }

        combine(
            paymentRepo.liveList(),
            receiptRepo.liveList(),
            userRepo.liveList().map {
                it.filter { user -> business.await().members.contains(user.documentID) }
            }
        ) { payments, receipts, users ->
            fun expensesByYear(year: Int) =
                payments.filter { it.date.year + 1900 == year }.sumOf { it.amount }

            fun revenueByYear(year: Int) =
                receipts.filter { it.date.year + 1900 == year }.sumOf { it.amount }

            fun netIncomeByYear(year: Int) = revenueByYear(year) - expensesByYear(year)
            fun revenueByMonthAndYear(month: Int, year: Int) = receipts
                .filter { it.date.month == month && it.date.year + 1900 == year }
                .sumOf { it.amount }

            fun expenditureByMonthAndYear(month: Int, year: Int) = payments
                .filter { it.date.month == month && it.date.year + 1900 == year }
                .sumOf { it.amount }

            fun netIncomeByMonthAndYear(month: Int, year: Int) =
                revenueByMonthAndYear(month, year) - expenditureByMonthAndYear(month, year)

            HomeState(
                revenueForYear = revenueByYear(selectedYear),
                expensesForYear = expensesByYear(selectedYear),
                incomeForYear = netIncomeByYear(selectedYear),
                incomeForMonth = netIncomeByMonthAndYear(selectedMonth, selectedYear),
                business = business.await(),
                accountUser = accountUser,
                selectedYear = selectedYear,
                users = users

            )
        }.collect { homeState ->
            Log.d(TAG, "HomeState COLLECTED")
            _homeState.update {
                it.copy(
                    revenueForYear = homeState.revenueForYear,
                    expensesForYear = homeState.expensesForYear,
                    incomeForYear = homeState.incomeForYear,
                    incomeForMonth = homeState.incomeForMonth,
                    business = homeState.business,
                    accountUser = homeState.accountUser,
                    selectedYear = homeState.selectedYear,
                    users = homeState.users
                )
            }
        }
    }

    companion object {
        const val TAG = "HomeViewModel"
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
