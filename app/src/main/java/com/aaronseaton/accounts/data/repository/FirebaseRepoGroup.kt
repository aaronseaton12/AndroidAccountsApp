package com.aaronseaton.accounts.data.repository

import android.util.Log
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.FirebaseEntity
import com.aaronseaton.accounts.domain.model.Matter
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import com.aaronseaton.accounts.domain.repository.RepoGroup.Repos
import com.aaronseaton.accounts.util.Constants.BUSINESSES
import com.aaronseaton.accounts.util.Constants.CUSTOMERS
import com.aaronseaton.accounts.util.Constants.MATTERS
import com.aaronseaton.accounts.util.Constants.PAYMENTS
import com.aaronseaton.accounts.util.Constants.RECEIPTS
import com.aaronseaton.accounts.util.Constants.TASKS
import com.aaronseaton.accounts.util.Constants.USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.full.memberProperties


@Singleton
class FirebaseRepoGroup@Inject constructor(
    firebase: FirebaseFirestore,
    userRepository: FirebaseUserRepository,
): RepoGroup {
    override val repos = userRepository.accountUser.distinctUntilChanged().map {
        val business = FireBaseEntityRepo(
            firebase,
            BUSINESSES,
            Business::class.java
        )
        val user = FireBaseEntityRepo(
            firebase,
            USERS,
            User::class.java
        )
        val receipt = FireBaseEntityRepo(
            firebase,
            "$BUSINESSES/${it.selectedBusiness}/$RECEIPTS",
            Receipt::class.java
        )
        val payment = FireBaseEntityRepo(
            firebase,
            "$BUSINESSES/${it.selectedBusiness}/$PAYMENTS",
            Payment::class.java
        )
        val task = FireBaseEntityRepo(
            firebase,
            "$BUSINESSES/${it.selectedBusiness}/$TASKS",
            Task::class.java
        )
        val customer = FireBaseEntityRepo(
            firebase,
            "$BUSINESSES/${it.selectedBusiness}/$CUSTOMERS",
            Customer::class.java
        )

        val matter = NumberedFirebaseEntityRepo(
            firebase,
            "$BUSINESSES/${it.selectedBusiness}/$MATTERS",
            Matter::class.java,
            it
        )
        Repos(
            it,
            user,
            business,
            customer,
            task,
            receipt,
            payment,
            matter
        )
    }
    companion object {
        private const val TAG = "TestRepo"
    }
}

fun<T> T.asMap() = this!!::class.memberProperties.associate { it.name to it.getter.call(this)}

class NumberedFirebaseEntityRepo<T: FirebaseEntity>(
    private val firebase: FirebaseFirestore,
    private val path:String,
    private val clazz: Class<T>,
    private val user: User,
): EntityRepo<T> by FireBaseEntityRepo(path=path, firebase = firebase, clazz = clazz) {
    private val counterPath = "counters"
    override suspend fun add(entity: T) = withContext(Dispatchers.IO) {
        Log.d(FireBaseEntityRepo.TAG, "${clazz.simpleName} Adding With Number: $entity")
        val counterName = getCounterName(clazz)
        val counterRef = firebase
            .collection(BUSINESSES)
            .document(user.selectedBusiness)
            .collection(counterPath)
            .document(counterName) //Change
        val collRef = firebase.collection(path)
        firebase.runTransaction { transaction ->
            try{
                val docRef = collRef.document()
                val counterSnapshot = transaction.get(counterRef)
                val number = counterSnapshot.getDouble("value")?: 0.0
                val newNumber = number + 1
                val entityMapWithNumber = entityToMap(entity, newNumber)
                Log.d(TAG, entityMapWithNumber.toString())
                transaction.set(docRef, entityMapWithNumber)
                transaction.set(counterRef, hashMapOf("value" to newNumber))
                Log.d(TAG, "There are now $number people living here")
            }catch (e: Exception) {
                Log.d("Transaction failed: ", e.message.toString())
            }
        }
        Unit
    }

    private fun entityToMap( entity: T, newNumber: Double ): MutableMap<String, Any?> {
        val entityMap = entity.asMap()
        val entityMapWithNumber = entityMap.toMutableMap()
        entityMapWithNumber["number"] = newNumber
        entityMapWithNumber.remove("documentID")
        return entityMapWithNumber
    }
    private fun getCounterName(clazz: Class<T>): String{
        val counterName = when (clazz) {
            Matter::class.java -> "matter"
            Payment::class.java -> "expense"
            Task::class.java -> "task"
            Receipt::class.java -> "income"
            Customer::class.java -> "contact"
            else -> clazz.simpleName.lowercase()
        }
        return counterName + "Counter"
    }

    companion object {
        const val TAG = "Numbered Entity Repo"
    }
}