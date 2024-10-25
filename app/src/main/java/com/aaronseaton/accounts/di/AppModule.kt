package com.aaronseaton.accounts.di

import com.aaronseaton.accounts.data.repository.AuthRepositoryImpl
import com.aaronseaton.accounts.data.repository.FirebaseRepoGroup
import com.aaronseaton.accounts.data.repository.FirebaseUserRepository
import com.aaronseaton.accounts.data.repository.ProfileImageRepositoryImpl
import com.aaronseaton.accounts.domain.repository.AuthRepository
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import com.aaronseaton.accounts.domain.repository.RepoGroup
import com.google.firebase.auth.FirebaseAuth
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

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    @Singleton
    fun providesUserRepository(
        auth: AuthRepository,
        db:FirebaseFirestore,
        //scope: CoroutineScope
    ): FirebaseUserRepository {
        return FirebaseUserRepository(auth, db)
    }
    @Provides
    @Singleton
    fun providesTestRepo(
        db: FirebaseFirestore,
        userRepository: FirebaseUserRepository
    ): RepoGroup {
        return FirebaseRepoGroup(db, userRepository)
    }

    @Provides
    fun provideProfileImageRepository(storage: FirebaseStorage, userRepo: FirebaseUserRepository):
        ProfileImageRepository = ProfileImageRepositoryImpl(
            storage = storage,
            userRepository = userRepo
        )


}