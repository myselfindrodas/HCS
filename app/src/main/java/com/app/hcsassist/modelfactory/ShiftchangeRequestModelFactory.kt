package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.viewmodel.ChangepasswordViewModel
import com.app.hcsassist.viewmodel.LoginViewModel
import com.app.hcsassist.viewmodel.ShiftchangeRequestViewModel
import com.app.hcsassist.viewmodel.UserdetailsViewModel
import com.example.wemu.repository.MainRepository

class ShiftchangeRequestModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        ShiftchangeRequestViewModel(MainRepository(apiHelper)) as T

}