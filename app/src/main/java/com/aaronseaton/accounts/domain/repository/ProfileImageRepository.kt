package com.aaronseaton.accounts.domain.repository

import android.net.Uri
import com.aaronseaton.accounts.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface ProfileImageRepository {

    suspend fun addImageToFirebaseStorage(
        imageUri: Uri, name: String
    ): Flow<Response<Uri>>

    suspend fun addImageUrlToFirestore(
        imageUrl: String,
        addFunction: (String) -> Unit
    ): Flow<Response<Boolean>>

    suspend fun getImageUrlFromFirestore(): Flow<Response<String>>

}