package com.aaronseaton.accounts.data.repository

import android.util.Log
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.FlowRepository
import com.aaronseaton.accounts.util.Constants.BUSINESSES
import com.aaronseaton.accounts.util.Constants.CUSTOMERS
import com.aaronseaton.accounts.util.Constants.PAYMENTS
import com.aaronseaton.accounts.util.Constants.RECEIPTS
import com.aaronseaton.accounts.util.Constants.TASKS
import com.aaronseaton.accounts.util.Constants.USERS
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FlowRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val currentUser: FirebaseUser
) : FlowRepository {
    //private val aaronSeatonBusiness = "4fWmC149N2x5Oz92Fv6s"
    private val userRef = db.collection(USERS)
    private val businessRef = db.collection(BUSINESSES)
    override var selectedBusinessID = ""
    override var accountUser = User()

    override suspend fun initialize() {
        accountUser = getAccountUser()
        selectedBusinessID = accountUser.selectedBusiness
    }

    private val customerRef
        get() = db.collection("$BUSINESSES/$selectedBusinessID/$CUSTOMERS")
    private val paymentRef
        get() = db.collection("$BUSINESSES/$selectedBusinessID/$PAYMENTS")
    private val receiptRef
        get() = db.collection("$BUSINESSES/$selectedBusinessID/$RECEIPTS")
    private val taskRef
        get() = db.collection("$BUSINESSES/$selectedBusinessID/$TASKS")

    override fun customerFlow(): Flow<List<Customer>> = flow {
        Log.d(TAG, "CustomerFlow(): Started")
        try {
            val response = customerRef.get().await().toObjects(Customer::class.java).also {
                Log.d(TAG, "CustomerFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception in Customers: ${exception.localizedMessage}")
        }
    }

    override fun paymentsFlow(): Flow<List<Payment>> = flow {
        Log.d(TAG, "PaymentsFlow(): Started")
        try {
            val response = paymentRef.get().await().toObjects(Payment::class.java).also {
                Log.d(TAG, "PaymentFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception in Payments: ${exception.localizedMessage}")
        }
    }

    override fun receiptsFlow(): Flow<List<Receipt>> = flow {
        Log.d(TAG, "ReceiptsFlow(): Started")
        try {
            val response = receiptRef.get().await().toObjects(Receipt::class.java).also {
                Log.d(TAG, "ReceiptFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception in Receipts: ${exception.localizedMessage}")
        }

    }

    override fun businessFlow(): Flow<List<Business>> = flow {
        Log.d(TAG, "BusinessFlow(): Started")
        try {
            val response = businessRef.get().await().toObjects(Business::class.java).also {
                Log.d(TAG, "BusinessFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception: ${exception.localizedMessage}")
        }

    }

    override fun taskFlow(): Flow<List<Task>> = flow {
        Log.d(TAG, "TaskFlow(): Started")
        try {
            val response = taskRef.get().await().toObjects(Task::class.java).also {
                Log.d(TAG, "TaskFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception: ${exception.localizedMessage}")
        }
    }

    override fun userFlow(): Flow<List<User>> = flow {
        Log.d(TAG, "UserFlow(): Started")
        try {
            val response = userRef.get().await().toObjects(User::class.java).also {
                Log.d(TAG, "UserFlow(): Successful")
            }
            emit(response)
        } catch (exception: Exception) {
            println("Exception: ${exception.localizedMessage}")
        }
    }

    override suspend fun getAccountUser(): User = withContext(Dispatchers.IO) {
        Log.d(TAG, "GetAccountUser(): Started")
        try {
            getUser(currentUser.uid).also { accountUser ->
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    if (accountUser.notificationTokens.contains(token)) {
                        Log.d(TAG, "Account Already Contains Notification Token")
                        return@addOnSuccessListener
                    } else {
                        accountUser.notificationTokens.add(token)
                        launch {
                            updateUser(accountUser)
                        }
                    }
                }
                Log.d(TAG, "Get Single ACCOUNT USER: SUCCESSFUL")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error")
            User()
        }
    }

    override suspend fun getCustomer(customerID: String): Customer = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Get Single Customer: STARTED")
            customerRef.document(customerID).get().await().toObject(Customer::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }

        } catch (e: Exception) {
            println("Get Customer Exception ${e.localizedMessage}")
            Customer()
        }
    }

    override suspend fun getUser(userID: String): User = withContext(Dispatchers.IO) {
        try {
            userRef.document(userID).get().await().toObject(User::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error")
            User()
        }
    }

    override suspend fun getPayment(paymentID: String): Payment = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Get Single Payment: STARTED")
            paymentRef.document(paymentID).get().await().toObject(Payment::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }

        } catch (e: Exception) {
            println("Get Payment Exception ${e.localizedMessage}")
            Payment()
        }
    }

    override suspend fun getReceipt(receiptID: String): Receipt = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Get Single Receipt: STARTED")
            receiptRef.document(receiptID).get().await().toObject(Receipt::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }
        } catch (e: Exception) {
            println("Get Receipt Exception ${e.localizedMessage}")
            Receipt()
        }
    }

    override suspend fun getBusiness(businessID: String): Business = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Get Single Business: STARTED")
            businessRef.document(businessID).get().await().toObject(Business::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }
        } catch (e: Exception) {
            println("Get Business Exception ${e.localizedMessage}")
            Business()
        }
    }

    override suspend fun getTask(taskID: String): Task = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Get Single Task: STARTED")
            taskRef.document(taskID).get().await().toObject(Task::class.java)!!.also {
                Log.d(TAG, "Get Single ${it.javaClass}: SUCCESSFUL")
            }

        } catch (e: Exception) {
            println("Get Task Exception ${e.localizedMessage}")
            Task()
        }
    }

    override suspend fun deletePayment(paymentID: String) {
        paymentRef.document(paymentID).delete().await()
    }

    override suspend fun deleteReceipt(receiptID: String) {
        receiptRef.document(receiptID).delete().await()
    }

    override suspend fun deleteTask(taskID: String) {
        taskRef.document(taskID).delete().await()
    }

    override suspend fun updateCustomer(customer: Customer) {
        Log.d(TAG, "Customer Snapshot UPDATED: ${customer.documentID}")
        customerRef.document(customer.documentID).set(customer)
    }

    override suspend fun updatePayment(payment: Payment) {
        Log.d(TAG, "Payment Snapshot UPDATED: ${payment.documentID}")
        paymentRef.document(payment.documentID).set(payment)
    }

    override suspend fun updateReceipt(receipt: Receipt) {
        Log.d(TAG, "Receipt Snapshot UPDATED: ${receipt.documentID}")
        receiptRef.document(receipt.documentID).set(receipt)
    }

    override suspend fun updateBusiness(business: Business) {
        Log.d(TAG, "Business Snapshot UPDATED: ${business.documentID}")
        businessRef.document(business.documentID).set(business)
    }

    override suspend fun updateUser(user: User) {
        Log.d(TAG, "User Snapshot UPDATED: ${user.documentID}")
        userRef.document(user.documentID).set(user)
    }

    override suspend fun updateTask(task: Task) {
        Log.d(TAG, "Task Snapshot UPDATED: ${task.documentID}")
        taskRef.document(task.documentID).set(task)
    }

    override suspend fun insertPayment(payment: Payment) {
        paymentRef.add(payment)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ${it.id})")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document", it)
            }
    }

    override fun insertCustomer(customer: Customer): String {
        val ref = customerRef.document()
        ref.set(customer)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ${ref.id})")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document", it)
            }
        Log.d(TAG, ref.id)
        return ref.id
    }

    override suspend fun insertReceipt(receipt: Receipt) {
        receiptRef.add(receipt)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ${it.id})")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document", it)
            }
    }

    override suspend fun insertBusiness(business: Business) {
        businessRef.add(business)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ${it.id})")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document", it)
            }
    }

    override suspend fun insertTask(task: Task) {
        taskRef.add(task)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot added with ${it.id})")
            }
            .addOnFailureListener {
                Log.w(TAG, "Error adding document", it)
            }
    }

    companion object {
        const val TAG: String = "FlowRepository"
    }
}