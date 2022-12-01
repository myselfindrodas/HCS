package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.LoginViewModel
import com.app.hcsassist.viewmodel.MyAttendanceViewModel
import com.example.wemu.repository.MainRepository

class MyAttendanceModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MyAttendanceViewModel(MainRepository(apiHelper)) as T

}