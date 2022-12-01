package com.app.hcsassist.modelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.retrofit.FaceRecogizationApiHelper
import com.app.hcsassist.viewmodel.LeaveViewModel
import com.app.hcsassist.viewmodel.RecognizationViewModel
import com.example.wemu.repository.FaceRecognizationRepository
import com.example.wemu.repository.MainRepository

class RecognizationModelFactory(private val faceRecogizationApiHelper: FaceRecogizationApiHelper) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        RecognizationViewModel(FaceRecognizationRepository(faceRecogizationApiHelper)) as T

}