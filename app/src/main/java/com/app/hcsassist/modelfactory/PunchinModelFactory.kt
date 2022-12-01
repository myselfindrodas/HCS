package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.LoginViewModel
import com.app.hcsassist.viewmodel.MyAttendanceViewModel
import com.app.hcsassist.viewmodel.PunchingViewModel
import com.example.wemu.repository.MainRepository

class PunchinModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PunchingViewModel(MainRepository(apiHelper)) as T

}