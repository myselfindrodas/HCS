package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {


    @POST("login")
    suspend fun login(
        @Header("appKey") appKey: String,
        @Body requestBody: LoginRequest
    ): LoginResponse


    @GET("my-profile")
    suspend fun myprofile(
        @Header("Authorization") Authorization: String
    ): UserdetailsResponse

    @Multipart
    @POST("upload-image")
    suspend fun uploadImage(
        @Header("Authorization") Authorization: String?,
        @Part file: MultipartBody.Part,
        ): UserdetailsResponse


    @GET("logout")
    suspend fun logout(
        @Header("Authorization") Authorization: String
    ): LogoutResponse


    @POST("change-password")
    suspend fun changepassword(
        @Header("Authorization") Authorization: String,
        @Body requestBody: ChangePasswordRequest
    ): ChangePasswordResponse


    @POST("shift-list")
    suspend fun shiftlist(
        @Header("Authorization") Authorization: String,
    ): ShiftListResponse


    @POST("shift-change-request")
    suspend fun shiftchangerequest(
        @Header("Authorization") Authorization: String,
        @Body requestBody: ShiftChangeRequest
    ): ShiftChangeResponse


    @GET("shift-change-request-list")
    suspend fun shiftchangelist(
        @Header("Authorization") Authorization: String,
    ): ShiftChangeListResponse


    @POST("shift-change-approval")
    suspend fun shiftchangeapproval(
        @Header("Authorization") Authorization: String,
        @Body requestBody: ShiftApprovalRequest
    ): ShiftApprovalResponse


    @GET("leave-type")
    suspend fun leavetype(
        @Header("Authorization") Authorization: String,
    ): LeavetypeResponse



    @Multipart
    @POST("leave-apply")
    suspend fun leaveapply(
        @Header("Authorization") Authorization: String?,
        @Part("leave_type_id") leave_type_id: RequestBody,
        @Part("leave_date_from") leave_date_from: RequestBody,
        @Part("leave_date_to") leave_date_to: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part file: MultipartBody.Part,
    ): LeaveapplyResponse



    @POST("my-leave")
    suspend fun myleave(
        @Header("Authorization") Authorization: String,
    ): AllLeaveResponse

}