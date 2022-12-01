package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class FaceRecogizationApiHelper(private val apiInterface2: ApiInterface2) {

    suspend fun face_compare(requestBody: FRSRequest) = apiInterface2.face_compare(requestBody)


}