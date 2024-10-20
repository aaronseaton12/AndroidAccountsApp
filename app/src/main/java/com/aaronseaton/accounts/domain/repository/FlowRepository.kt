package com.aaronseaton.accounts.domain.repository

import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import kotlinx.coroutines.flow.Flow

interface FlowRepository {
    var selectedBusinessID: String
    suspend fun initialize()
    fun customerFlow(): Flow<List<Customer>>
    fun receiptsFlow(): Flow<List<Receipt>>
    fun paymentsFlow(): Flow<List<Payment>>
    fun businessFlow(): Flow<List<Business>>
    fun taskFlow(): Flow<List<Task>>
    fun userFlow(): Flow<List<User>>

    suspend fun getAccountUser(): User
    suspend fun getUser(userID: String): User
    suspend fun getCustomer(customerID: String): Customer
    suspend fun getPayment(paymentID: String): Payment
    suspend fun getReceipt(receiptID: String): Receipt
    suspend fun getBusiness(businessID: String): Business
    suspend fun getTask(taskID: String): Task

    suspend fun deletePayment(paymentID: String)
    suspend fun deleteReceipt(receiptID: String)
    suspend fun deleteTask(taskID: String)

    suspend fun updatePayment(payment: Payment)
    suspend fun updateCustomer(customer: Customer)
    suspend fun updateReceipt(receipt: Receipt)
    suspend fun updateBusiness(business: Business)
    suspend fun updateUser(user: User)
    suspend fun updateTask(task: Task)

    suspend fun insertPayment(payment: Payment)
    fun insertCustomer(customer: Customer): String
    suspend fun insertReceipt(receipt: Receipt)
    suspend fun insertBusiness(business: Business)
    suspend fun insertTask(task: Task)
    var accountUser: User
}