package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.CalenderEventViewModel
import com.app.hcsassist.viewmodel.LoginViewModel
import com.app.hcsassist.viewmodel.MyAttendanceViewModel
import com.example.wemu.repository.MainRepository

class CalenderEventModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CalenderEventViewModel(MainRepository(apiHelper)) as T

}