package com.app.hcsassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.hcsassist.apimodel.LoginRequest
import com.app.hcsassist.retrofit.Resource
import com.example.wemu.repository.MainRepository
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun login(appKey: String, requestBody: LoginRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.login(appKey,requestBody)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }
}