package com.example.digitalbankkyc.ui.accounts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalbankkyc.data.repository.CustomerRepository
import com.example.digitalbankkyc.domain.model.Customer
import com.example.digitalbankkyc.domain.model.KycStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AccountsUiState {
    object Loading : AccountsUiState()
    data class Success(val customers: List<Customer>) : AccountsUiState()
    data class Error(val message: String) : AccountsUiState()
}

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<AccountsUiState>()
    val uiState: LiveData<AccountsUiState> = _uiState

    private val _searchQuery = MutableLiveData("")
    private var allCustomers: List<Customer> = emptyList()

    init {
        loadCustomers()
    }

    fun loadCustomers(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                // First show cached data instantly if available
                val cached = repository.getCachedCustomers()
                if (cached.isNotEmpty()) {
                    allCustomers = cached
                    applySearch(_searchQuery.value ?: "")
                } else {
                    _uiState.value = AccountsUiState.Loading
                }

                // Then fetch fresh data in background
                allCustomers = repository.getCustomers(forceRefresh)
                applySearch(_searchQuery.value ?: "")

            } catch (e: Exception) {
                if (allCustomers.isEmpty()) {
                    _uiState.value = AccountsUiState.Error(
                        "Failed: ${e.message}"
                    )
                }
            }
        }
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun search(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            kotlinx.coroutines.delay(300) // wait 300ms before searching
            applySearch(query)
        }
    }

    private fun applySearch(query: String) {
        val filtered = if (query.isBlank()) {
            allCustomers
        } else {
            allCustomers.filter {
                val fullName = "${it.firstName} ${it.lastName}"
                fullName.contains(query, ignoreCase = true) ||
                        it.iban.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = AccountsUiState.Success(filtered)
    }

    fun getVerifiedCustomers(customers: List<Customer>) =
        customers.filter { it.kycStatus == KycStatus.VERIFIED }

    fun getPendingCustomers(customers: List<Customer>) =
        customers.filter { it.kycStatus == KycStatus.PENDING }
}