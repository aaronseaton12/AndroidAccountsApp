package com.aaronseaton.accounts.presentation.advancedstats
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FinancialTransaction
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.repository.EntityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class AdvancedStatsState(
    val netIncomeByYearMonth: List<Pair<YearMonth, Double>> = emptyList(),
    val highestPaidVendors: List<Pair<String, Double>> = emptyList(),
    val highestPayingCustomers: List<Pair<String, Double>> = emptyList(),
    val averageRevenuePayment: Double = 0.0,
    val averageExpensePayment: Double = 0.0,
    val loading: Boolean = false
)

@HiltViewModel
class AdvancedStatsViewModel @Inject constructor(
    private val customerRepo: EntityRepo<Customer>,
    private val receiptRepo: EntityRepo<Receipt>,
    private val paymentRepo: EntityRepo<Payment>,
) : ViewModel() {

    private val _state = MutableStateFlow(AdvancedStatsState(loading = true))
    val state: StateFlow<AdvancedStatsState> = _state.asStateFlow()

    init {
        updateAdvancedStatsState()
    }

    private fun updateAdvancedStatsState() {
        viewModelScope.launch {
            combine(
                customerRepo.liveList(),
                paymentRepo.liveList(),
                receiptRepo.liveList()
            ) { customers, payments, receipts ->
                val highestPayingCustomers = contactsRankedByTransactionValue(customers, receipts)
                val highestPaidVendors = contactsRankedByTransactionValue(customers, payments)
                AdvancedStatsState(
                    netIncomeByYearMonth = netIncomeByYearMonth(receipts, payments),
                    highestPaidVendors = highestPaidVendors,
                    highestPayingCustomers = highestPayingCustomers,
                    averageRevenuePayment = highestPayingCustomers.toMap().values.average(),
                    averageExpensePayment = highestPaidVendors.toMap().values.average(),
                    loading = false
                )
            }.collect { advancedStatsState ->
                _state.value = advancedStatsState
            }
        }
    }

    private fun netIncomeByYearMonth(
        incomes: List<FinancialTransaction>,
        expenses: List<FinancialTransaction>
    ): List<Pair<YearMonth, Double>> {
        val allTransactions = incomes + expenses
        val firstTransDate = allTransactions
            .minByOrNull { it.date }?.date
            ?.toInstant()
            ?.atZone(ZoneId.systemDefault())
            ?.toLocalDate() ?: LocalDate.now()

        val firstTransYearMonth = YearMonth.of(firstTransDate.year, firstTransDate.month)
        val numberOfMonthToDate = firstTransYearMonth.numberMonthsToDate()
        val yearMonthsToDate = (0..numberOfMonthToDate).flatMap {
            listOf(firstTransYearMonth.plusMonths(it))
        }
        val netIncomeByYearMonth = yearMonthsToDate.map { yearMonth ->
            val receiptsByMonth = incomes
                .filter { it.isWithinYearMonth(yearMonth) }
                .sumOf { it.amount }
            val expensesByMonth = expenses
                .filter { it.isWithinYearMonth(yearMonth) }
                .sumOf { it.amount }
            val netMonthlyIncome = receiptsByMonth - expensesByMonth
            yearMonth to netMonthlyIncome
        }
        return netIncomeByYearMonth
    }

    private fun contactsRankedByTransactionValue(
        contacts: List<Customer>,
        transactions: List<FinancialTransaction>,
    ): List<Pair<String, Double>> {
        return transactions
            .groupBy { it.customerID }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            .mapKeys { map -> contacts.find { it.documentID == map.key }!!.fullName() }
            .toList()
            .sortedByDescending { it.second }
    }

    private fun FinancialTransaction.isWithinYearMonth(yearDate: YearMonth): Boolean {
        val isMonthEqual = this.date.month + 1 == yearDate.month.value
        val isYearEqual = this.date.year + 1900 == yearDate.year
        val areMonthAndYearEqual = isYearEqual && isMonthEqual
        return areMonthAndYearEqual
    }

    private fun YearMonth.numberMonthsToDate(otherYearMonth: YearMonth = YearMonth.now()): Long {
        return ChronoUnit.MONTHS.between(this, otherYearMonth)
    }
}

//data class AdvancedStatsState(
//    val customers: List<Customer> = listOf(Customer()),
//    val payments: List<Payment> = listOf(Payment()),
//    val receipts: List<Receipt> = listOf(Receipt()),
//    val loading: Boolean = false
//)
