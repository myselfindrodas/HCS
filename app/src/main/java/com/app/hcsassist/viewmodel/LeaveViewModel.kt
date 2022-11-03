package com.app.hcsassist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.app.hcsassist.retrofit.Resource
import com.example.wemu.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LeaveViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun leavetype(authtoken: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.leavetype(authtoken)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

    fun leaveapply(
        authtoken: String,
        leave_type_id: RequestBody,
        leave_date_from: RequestBody,
        leave_date_to:RequestBody,
        comment:RequestBody,
        part: MultipartBody.Part) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))

        try {
            emit(Resource.success(data = mainRepository.leaveapply(authtoken,
                leave_type_id,
                leave_date_from,
                leave_date_to,
                comment,
                part)))
        } catch (e: Exception) {
            emit(Resource.error(data = null, message = e.message ?: "Error Occurred!"))
        }
    }

}