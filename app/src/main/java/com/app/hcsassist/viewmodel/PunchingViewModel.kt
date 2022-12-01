package com.app.hcsassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.hcsassist.apimodel.FRSRequest
import com.app.hcsassist.apimodel.LoginRequest
import com.app.hcsassist.apimodel.PunchinRequest
import com.app.hcsassist.apimodel.PunchoutRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.retrofit.Resource
import com.example.wemu.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.RequestBody

class PunchingViewModel(private val mainRepository: MainRepository) : ViewModel()  {
    fun punchin(authtoken: String, requestBody: PunchinRequest) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.punchin(authtoken,requestBody)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }


    fun punchout(authtoken: String, requestBody: PunchoutRequest, id:String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.punchout(authtoken,requestBody, id)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }




}