package com.app.hcsassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.hcsassist.apimodel.ChangePasswordRequest
import com.app.hcsassist.apimodel.LoginRequest
import com.app.hcsassist.retrofit.Resource
import com.example.wemu.repository.MainRepository
import kotlinx.coroutines.Dispatchers

class ShiftListViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun shiftlist(authtoken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.shiftlist(authtoken)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }
}