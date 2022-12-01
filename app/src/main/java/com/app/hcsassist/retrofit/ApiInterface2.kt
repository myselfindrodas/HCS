package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListResponse
import com.app.hcsassist.apimodel.calender_event.CalenderEventResponseModel
import com.app.hcsassist.apimodel.location.LocationListResponse
import com.app.hcsassist.apimodel.mss_attendance.MSSAttendance_ResponseModel
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceResponseModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface2 {

    @POST("face_compare/")
    suspend fun face_compare(
        @Body requestBody: FRSRequest
    ): RecognizationRequest


}