package com.example.wemu.repository

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.retrofit.ApiHelper
import com.app.hcsassist.retrofit.FaceRecogizationApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FaceRecognizationRepository(private val faceRecogizationApiHelper: FaceRecogizationApiHelper) {

    suspend fun face_compare(requestBody: FRSRequest) = faceRecogizationApiHelper.face_compare(requestBody)

}