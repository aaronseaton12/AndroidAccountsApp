package com.aaronseaton.accounts.di

import com.aaronseaton.accounts.data.repository.AuthRepositoryImpl
import com.aaronseaton.accounts.data.repository.ProfileImageRepositoryImpl
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Customer
import com.aaronseaton.accounts.domain.model.Payment
import com.aaronseaton.accounts.domain.model.Receipt
import com.aaronseaton.accounts.domain.model.Task
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.AuthRepository
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.FireBaseEntityRepo
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import com.aaronseaton.accounts.domain.repository.UserRepository
import com.aaronseaton.accounts.util.Constants.BUSINESSES
import com.aaronseaton.accounts.util.Constants.CUSTOMERS
import com.aaronseaton.accounts.util.Constants.PAYMENTS
import com.aaronseaton.accounts.util.Constants.RECEIPTS
import com.aaronseaton.accounts.util.Constants.TASKS
import com.aaronseaton.accounts.util.Constants.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val TAG: String = "AppModule"

    @Provides
    @Singleton
    fun providesCoroutineContext() = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun providesFireBaseFireStore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun providesCurrentUser() = Firebase.auth.currentUser!!

    @Provides
    @Singleton
    fun providesFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun providesAuthenticator(auth: FirebaseAuth): AuthRepository {
        val authRepo = AuthRepositoryImpl(auth)
        authRepo.addAuthStateListener{
            val message = if(it != null) "${it.firstName} Signed in" else "Sign Out"
            println("State Change Listener invoked: $message")
        }
        return authRepo
    }

//    @Provides
//    @Singleton
//    fun providesFlowRepository(db: FirebaseFirestore, currentUser: FirebaseUser): FlowRepository {
//        return FlowRepositoryImpl(db, currentUser)
//    }

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage
    @Provides
    @Singleton
    fun providesAccountUser(userRepo: EntityRepo<User>, currentUser: FirebaseUser): User {
        return runBlocking { userRepo.get(currentUser.uid) }
    }
    @Provides
    @Singleton
    fun providesPaymentRepo (
        db: FirebaseFirestore,
        accountUser: User
    ): EntityRepo<Payment> {
        return FireBaseEntityRepo(
            db,
            "$BUSINESSES/${accountUser.selectedBusiness}/$PAYMENTS",
            Payment::class.java)
    }

    @Provides
    @Singleton
    fun providesReceiptRepo (
        db: FirebaseFirestore,
        accountUser: User
    ): EntityRepo<Receipt> {
        return FireBaseEntityRepo(
            db,
            "$BUSINESSES/${accountUser.selectedBusiness}/$RECEIPTS",
            Receipt::class.java)
    }
    @Provides
    @Singleton
    fun providesTaskRepo (
        db: FirebaseFirestore,
        accountUser: User
    ): EntityRepo<Task> {
        return FireBaseEntityRepo(
            db,
            "$BUSINESSES/${accountUser.selectedBusiness}/$TASKS",
            Task::class.java)
    }

    @Provides
    @Singleton
    fun providesCustomerRepo (
        db: FirebaseFirestore,
        accountUser: User
    ): EntityRepo<Customer> {
        return FireBaseEntityRepo(
            db,
            "$BUSINESSES/${accountUser.selectedBusiness}/$CUSTOMERS",
            Customer::class.java
        )
    }

    @Provides
    @Singleton
    fun providesBusinessRepo (
        db: FirebaseFirestore
    ): EntityRepo<Business> {
        return FireBaseEntityRepo(
            db,
            BUSINESSES,
            Business::class.java)
    }
    @Provides
    @Singleton
    fun providesUserRepo (
        db: FirebaseFirestore
    ): EntityRepo<User> {
        return FireBaseEntityRepo(
            db,
            USERS,
            User::class.java)
    }
    fun providesUserRepo(auth: AuthRepository, db:FirebaseFirestore): UserRepository{
        return UserRepository(auth, db)
    }

    @Provides
    fun provideProfileImageRepository(storage: FirebaseStorage, accountUser: User):
        ProfileImageRepository = ProfileImageRepositoryImpl(
            storage = storage,
            accountUser = accountUser
        )

}