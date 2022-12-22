package com.example.wemu.repository

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import com.app.hcsassist.retrofit.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun login(appKey: String, requestBody: LoginRequest) =
        apiHelper.login(appKey, requestBody)

    suspend fun userdetails(authtoken: String) = apiHelper.userdetails(authtoken)
    suspend fun picupload(authtoken: String, part: MultipartBody.Part) =
        apiHelper.picupload(authtoken, part)

    suspend fun logout(authtoken: String, requestBody: LogoutRequest) = apiHelper.logout(authtoken, requestBody)
    suspend fun changepassword(authtoken: String, requestBody: ChangePasswordRequest) =
        apiHelper.changepassword(authtoken, requestBody)

    suspend fun shiftlist(authtoken: String) = apiHelper.shiftlist(authtoken)
    suspend fun shiftchangerequest(authtoken: String, requestBody: ShiftChangeRequest) =
        apiHelper.shiftchangerequest(authtoken, requestBody)

    suspend fun shiftchangelist(authtoken: String, page: String) = apiHelper.shiftchangelist(authtoken, page)

    suspend fun shiftchangeapproval(authtoken: String, requestBody: ShiftApprovalRequest) =
        apiHelper.shiftchangeapproval(authtoken, requestBody)

    suspend fun leavetype(authtoken: String) = apiHelper.leavetype(authtoken)
    suspend fun leaveapply(
        authtoken: String,
        leave_type_id: RequestBody,
        id: String,
        leave_date_from: RequestBody,
        leave_date_to: RequestBody,
        comment: RequestBody,
        part: MultipartBody.Part
    ) =
        apiHelper.leaveapply(
            authtoken,
            leave_type_id,
            id,
            leave_date_from,
            leave_date_to,
            comment,
            part
        )


    suspend fun leaveapplywithoutdoc(
        authtoken: String,
        leave_type_id: RequestBody,
        id: String,
        leave_date_from: RequestBody,
        leave_date_to: RequestBody,
        comment: RequestBody
    ) =
        apiHelper.leaveapplywithoutdoc(
            authtoken,
            leave_type_id,
            id,
            leave_date_from,
            leave_date_to,
            comment
        )

    suspend fun leavelist(authtoken: String) = apiHelper.leavelist(authtoken)
    suspend fun requestedleavelist(authtoken: String, page: String) = apiHelper.requestedleavelist(authtoken, page)

    suspend fun approveleave(authtoken: String, requestBody: LeaveApprovalRequest) =
        apiHelper.approveleave(authtoken, requestBody)

    suspend fun uploadfrs(authtoken: String, requestBody: FRSuploadRequest) = apiHelper.uploadfrs(authtoken, requestBody)
    suspend fun leavecancel(authtoken: String, requestBody: CancelLeaveRequest) = apiHelper.leavecancel(authtoken, requestBody)
    suspend fun myAttendanceList(authtoken: String, requestBody: MyAttendanceRequest) = apiHelper.myAttendance(authtoken,requestBody)
    suspend fun punchin(authtoken: String, requestBody: PunchinRequest) = apiHelper.punchin(authtoken,requestBody)
    suspend fun punchout(authtoken: String, requestBody: PunchoutRequest, id: String) = apiHelper.punchout(authtoken,requestBody, id)
    suspend fun getCalenderEvent(authtoken: String, requestBody: CalenderEventRequest) = apiHelper.calenderEvent(authtoken,requestBody)
    suspend fun getLocationList(authtoken: String) = apiHelper.locationList(authtoken)
    suspend fun getAdminHr(authtoken: String, requestBody: AdminHrListRequest) = apiHelper.adminHrList(authtoken,requestBody)
    suspend fun getMSSAttendanceList(authtoken: String, requestBody: MssAttendanceRequest) = apiHelper.mssAttendanceList(authtoken,requestBody)
    suspend fun selfshiftchange(authtoken: String, requestBody: SelfShiftChangeRequest) = apiHelper.selfshiftchange(authtoken,requestBody)
    suspend fun myshiftchangerequestlist(authtoken: String) = apiHelper.myshiftchangerequestlist(authtoken)




}