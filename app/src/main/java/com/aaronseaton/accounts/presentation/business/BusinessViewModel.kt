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
import com.aaronseaton.accounts.domain.repository.ProfileImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class BusinessViewModel @Inject constructor(
    private val accountUser: User,
    private val userRepo: EntityRepo<User>,
    private val businessRepo: EntityRepo<Business>,
    private val repo: ProfileImageRepository
) : ViewModel() {
    private val _businessListState = MutableStateFlow(BusinessListState(loading = true))
    val businessListState: StateFlow<BusinessListState> = _businessListState.asStateFlow()

    private val _individualBusinessState = MutableStateFlow(IndividualBusinessState(loading = true))
    val individualBusinessState: StateFlow<IndividualBusinessState> =
        _individualBusinessState.asStateFlow()

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

    init {
        refreshList()
        refreshIndividual()
    }

    private fun refreshIndividual() = viewModelScope.launch {
        val accountUser = accountUser
        _individualBusinessState.update {
            it.copy(
                accountUser = accountUser,
                loading = false
            )
        }
    }

    fun getBusiness(businessID: String) = viewModelScope.launch {
        val business = businessRepo.get(businessID)
        _individualBusinessState.update {
            it.copy(
                business = business
            )
        }
        userRepo.liveList().map {
            it.filter { user -> business.members.contains(user.documentID) }
        }.collect { users ->
            _individualBusinessState.update {
                it.copy(
                    users = users,
                    loading = false
                )
            }
        }
    }


    private fun refreshList() = viewModelScope.launch {
        val user = accountUser
        businessRepo.liveList()
            .map { it.filter { business -> business.members.contains(user.documentID) } }
            .collect { businesses ->
                _businessListState.update {
                    it.copy(
                        businesses = businesses,
                        loading = false
                    )
                }
            }
    }

    fun updateBusiness(business: Business) = viewModelScope.launch {
        businessRepo.update(business.documentID, business)
    }

    fun insertBusiness(business: Business) = viewModelScope.launch {
        businessRepo.add(business)
    }
}

data class IndividualBusinessState(
    val accountUser: User = User(),
    val users: List<User> = listOf(User()),
    val business: Business = Business(),
    val loading: Boolean = false
)

data class BusinessListState(
    val businesses: List<Business> = listOf(Business()),
    val loading: Boolean = false
)