package com.aaronseaton.accounts.presentation.business

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaronseaton.accounts.domain.model.Business
import com.aaronseaton.accounts.domain.model.Response
import com.aaronseaton.accounts.domain.model.User
import com.aaronseaton.accounts.domain.repository.EntityRepo
import com.aaronseaton.accounts.domain.repository.RepoGroup
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class BusinessViewModel @Inject constructor(
    private val repoGroup: RepoGroup,
    private val repo: ProfileImageRepository
) : ViewModel() {

    private var businessRepo: EntityRepo<Business>? = null
    private var businessID: String? = null
    fun setBusinessId(businessID: String?) {this.businessID = businessID}

    val state = repoGroup.repos.map { repos ->
        businessRepo = repos.business
        BusinessListState(
            repos.business.list(),
            loading = false
        )
    }
    val individualState = repoGroup.repos.map { repos ->
        businessRepo = repos.business
        val business = if(businessID==null)Business() else repos.business.get(businessID!!)
        IndividualBusinessState(
            business = business,
            accountUser = repos.accountUser,
            users = repos.user.list().filter { business.members.contains(it.documentID) },
            loading = false
        )
    }


    var addImageToStorageResponse by mutableStateOf<Response<Uri>>(Response.Success(null))
        private set
    var addImageToDatabaseResponse by mutableStateOf<Response<Boolean>>(Response.Success(null))
        private set
    var getImageFromDatabaseResponse by mutableStateOf<Response<String>>(Response.Success(null))
        private set

    fun addImageToStorage(imageUri: Uri, name: String) = viewModelScope.launch {
        repo.addImageToFirebaseStorage(imageUri, name).collect { response ->
            addImageToStorageResponse = response
        }
    }

    fun addImageToDatabase(business: Business, downloadUrl: Uri) = viewModelScope.launch {
        repo.addImageUrlToFirestore(downloadUrl.toString()) { imageUrl ->
            updateBusiness(business.copy(logo = imageUrl))
        }
            .collect { response ->
                addImageToDatabaseResponse = response
            }
    }

    fun getImageFromDatabase() = viewModelScope.launch {
        repo.getImageUrlFromFirestore().collect { response ->
            getImageFromDatabaseResponse = response
        }
    }



    fun updateBusiness(business: Business) = viewModelScope.launch {
        businessRepo?.update(business.documentID, business)
    }

    fun insertBusiness(business: Business) = viewModelScope.launch {
        businessRepo?.add(business)
    }
}

data class IndividualBusinessState(
    val accountUser: User = User(),
    val users: List<User> = listOf(User()),
    val business: Business = Business(),
    val loading: Boolean = true
)

data class BusinessListState(
    val businesses: List<Business> = listOf(Business()),
    val loading: Boolean = true
)