package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceResponseModel
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
    @POST("leave-apply/{Id}")
    suspend fun leaveapply(
        @Header("Authorization") Authorization: String?,
        @Part("leave_type_id") leave_type_id: RequestBody,
        @Path("Id") id:String,
        @Path("leave_date_from") leave_date_from: RequestBody,
        @Part("leave_date_to") leave_date_to: RequestBody,
        @Part("comment") comment: RequestBody,
        @Part file: MultipartBody.Part,
    ): LeaveapplyResponse


    @Multipart
    @POST("leave-apply/{Id}")
    suspend fun leaveapplywithoutdoc(
        @Header("Authorization") Authorization: String?,
        @Part("leave_type_id") leave_type_id: RequestBody,
        @Path("Id") id:String,
        @Part("leave_date_from") leave_date_from: RequestBody,
        @Part("leave_date_to") leave_date_to: RequestBody,
        @Part("comment") comment: RequestBody
    ): LeaveapplyResponse


    @GET("my-leave")
    suspend fun myleave(
        @Header("Authorization") Authorization: String,
    ): AllLeaveResponse



    @GET("requested-leave-list")
    suspend fun requestedleavelist(
        @Header("Authorization") Authorization: String,
    ): LeaveResponse


    @POST("approve-leave")
    suspend fun approveleave(
        @Header("Authorization") Authorization: String,
        @Body requestBody: LeaveApprovalRequest
    ): ShiftApprovalResponse


    @POST("cancel-leave")
    suspend fun leavecancel(
        @Header("Authorization") Authorization: String,
        @Body requestBody: CancelLeaveRequest
    ): LeaveCancelResponse


    @POST("upload-frs")
    suspend fun uploadfrs(
        @Header("Authorization") Authorization: String?,
        @Body requestBody: FRSuploadRequest
    ): FRSResponse


    @POST("my-attendance-list")
    suspend fun myAttendance(
        @Header("Authorization") Authorization: String,
        @Body requestBody: MyAttendanceRequest
    ): MyAttendanceResponseModel


    @POST("punch-in")
    suspend fun punchin(
        @Header("Authorization") Authorization: String,
        @Body requestBody: PunchinRequest
    ): PunchinResponse


    @POST("punch-out/{id}")
    suspend fun punchout(
        @Header("Authorization") Authorization: String?,
        @Body requestBody: PunchoutRequest,
        @Path("id") id:String,
    ): PunchoutResponse

}