package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.AdminHrListViewModel
import com.app.hcsassist.viewmodel.LoginViewModel
import com.app.hcsassist.viewmodel.MssAttendanceListViewModel
import com.app.hcsassist.viewmodel.MyAttendanceViewModel
import com.example.wemu.repository.MainRepository

class MssAttendanceModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MssAttendanceListViewModel(MainRepository(apiHelper)) as T

}