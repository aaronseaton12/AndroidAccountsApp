package com.aaronseaton.accounts.data.repository

import android.net.Uri
import com.aaronseaton.accounts.domain.model.Response
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import com.aaronseaton.accounts.util.Constants.IMAGES
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val userRepository: FirebaseUserRepository,
) : ProfileImageRepository {
    var accountUser = User()

    init {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO);
        scope.launch {
            userRepository.accountUser.collectLatest{
                accountUser = it
            }
        }
    }
    override suspend fun addImageToFirebaseStorage(imageUri: Uri, name: String) = flow {
        try {
            emit(Response.Loading)
            val downloadUrl = storage
                .reference
                .child(IMAGES)
                .child(accountUser.documentID)
                .child(name)
                .putFile(imageUri).await()
                .storage.downloadUrl.await()
            emit(Response.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

    override suspend fun addImageUrlToFirestore(imageUrl: String, addFunction: (String) -> Unit) =
        flow {
            try {
                emit(Response.Loading)
                addFunction(imageUrl)
                emit(Response.Success(true))
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }

    override suspend fun getImageUrlFromFirestore() = flow {
        try {
            emit(Response.Loading)
            val imageUrl = accountUser.photoUrl
            emit(Response.Success(imageUrl))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }
}