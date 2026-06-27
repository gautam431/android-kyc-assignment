package com.example.digitalbankkyc.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalbankkyc.data.model.IfscResponse
import com.example.digitalbankkyc.data.repository.CustomerRepository
import com.example.digitalbankkyc.domain.model.Customer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val customer: Customer) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

sealed class IfscUiState {
    object Loading : IfscUiState()
    data class Success(val data: IfscResponse) : IfscUiState()
    data class Error(val message: String) : IfscUiState()
}

@HiltViewModel
class AccountDetailViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _detailState = MutableLiveData<DetailUiState>()
    val detailState: LiveData<DetailUiState> = _detailState

    private val _ifscState = MutableLiveData<IfscUiState>()
    val ifscState: LiveData<IfscUiState> = _ifscState

    fun loadCustomer(id: Int) {
        _detailState.value = DetailUiState.Loading
        viewModelScope.launch {
            try {
                val customer = repository.getCustomerById(id)
                if (customer != null) {
                    _detailState.value = DetailUiState.Success(customer)
                    loadIfscDetails(customer.ifscCode)
                } else {
                    _detailState.value = DetailUiState.Error("Customer not found")
                }
            } catch (e: Exception) {
                _detailState.value = DetailUiState.Error(
                    e.message ?: "Failed to load customer"
                )
            }
        }
    }

    private fun loadIfscDetails(ifsc: String) {
        _ifscState.value = IfscUiState.Loading
        viewModelScope.launch {
            try {
                val response = repository.getIfscDetails(ifsc)
                _ifscState.value = IfscUiState.Success(response)
            } catch (e: Exception) {
                _ifscState.value = IfscUiState.Error("Could not resolve IFSC")
            }
        }
    }

    fun refreshCustomer(id: Int) {
        loadCustomer(id)
    }
}