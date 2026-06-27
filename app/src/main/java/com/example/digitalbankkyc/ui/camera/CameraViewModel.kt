package com.example.digitalbankkyc.ui.camera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitalbankkyc.data.repository.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: CustomerRepository
) : ViewModel() {

    private val _kycCompleted = MutableLiveData(false)
    val kycCompleted: LiveData<Boolean> = _kycCompleted

    fun completeKyc(customerId: Int, selfiePath: String) {
        viewModelScope.launch {
            try {
                repository.updateKycVerified(customerId, selfiePath)
                _kycCompleted.postValue(true)
            } catch (e: Exception) {
                _kycCompleted.postValue(false)
            }
        }
    }
}