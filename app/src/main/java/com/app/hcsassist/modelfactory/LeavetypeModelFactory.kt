package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.LeaveViewModel
import com.example.wemu.repository.MainRepository

class LeavetypeModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        LeaveViewModel(MainRepository(apiHelper)) as T

}