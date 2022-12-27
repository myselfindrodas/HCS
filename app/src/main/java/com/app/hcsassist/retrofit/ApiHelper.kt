package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.apimodel.admin_hr_response_model.AdminHrListRequest
import com.app.hcsassist.apimodel.myattendance.CalenderEventRequest
import com.app.hcsassist.apimodel.myattendance.MssAttendanceRequest
import com.app.hcsassist.apimodel.myattendance.MyAttendanceRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiHelper(private val apiInterface: ApiInterface) {

    suspend fun login(appKey: String, requestBody: LoginRequest) =
        apiInterface.login(appKey, requestBody)

    suspend fun userdetails(authtoken: String) = apiInterface.myprofile(authtoken)
    suspend fun picupload(authtoken: String, part: MultipartBody.Part) =
        apiInterface.uploadImage(authtoken, part)

    suspend fun logout(authtoken: String, requestBody: LogoutRequest) = apiInterface.logout(authtoken, requestBody)
    suspend fun changepassword(authtoken: String, requestBody: ChangePasswordRequest) =
        apiInterface.changepassword(authtoken, requestBody)

    suspend fun shiftlist(authtoken: String) = apiInterface.shiftlist(authtoken)
    suspend fun shiftchangerequest(authtoken: String, requestBody: ShiftChangeRequest) =
        apiInterface.shiftchangerequest(authtoken, requestBody)

    suspend fun shiftchangelist(authtoken: String) = apiInterface.shiftchangelist(authtoken)
    suspend fun shiftchangeapproval(authtoken: String, requestBody: ShiftApprovalRequest) =
        apiInterface.shiftchangeapproval(authtoken, requestBody)

    suspend fun leavetype(authtoken: String) = apiInterface.leavetype(authtoken)
    suspend fun leaveapply(
        authtoken: String,
        leave_type_id: RequestBody,
        id: String,
        leave_date_from: RequestBody,
        leave_date_to: RequestBody,
        comment: RequestBody,
        part: MultipartBody.Part
    ) =
        apiInterface.leaveapply(
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
        apiInterface.leaveapplywithoutdoc(
            authtoken,
            leave_type_id,
            id,
            leave_date_from,
            leave_date_to,
            comment
        )


    suspend fun leavelist(authtoken: String) = apiInterface.myleave(authtoken)
    suspend fun requestedleavelist(authtoken: String) = apiInterface.requestedleavelist(authtoken)
    suspend fun approveleave(authtoken: String, requestBody: LeaveApprovalRequest) =
        apiInterface.approveleave(authtoken, requestBody)

    suspend fun uploadfrs(authtoken: String, requestBody: FRSuploadRequest) = apiInterface.uploadfrs(authtoken, requestBody)
    suspend fun leavecancel(authtoken: String, requestBody: CancelLeaveRequest) = apiInterface.leavecancel(authtoken, requestBody)
    suspend fun myAttendance(authtoken: String, requestBody: MyAttendanceRequest) = apiInterface.myAttendance(authtoken,requestBody)
    suspend fun punchin(authtoken: String, requestBody: PunchinRequest) = apiInterface.punchin(authtoken,requestBody)
    suspend fun punchout(authtoken: String, requestBody: PunchoutRequest, id: String) = apiInterface.punchout(authtoken,requestBody, id)

    suspend fun calenderEvent(authtoken: String, requestBody: CalenderEventRequest) = apiInterface.calenderEvent(authtoken,requestBody)
    suspend fun adminHrList(authtoken: String, requestBody: AdminHrListRequest) = apiInterface.adminHrUser(authtoken,requestBody)
    suspend fun locationList(authtoken: String) = apiInterface.locationList(authtoken)
    suspend fun mssAttendanceList(authtoken: String, requestBody: MssAttendanceRequest) = apiInterface.mssAttendancelist(authtoken,requestBody)
    suspend fun selfshiftchange(authtoken: String, requestBody: SelfShiftChangeRequest) = apiInterface.selfshiftchange(authtoken,requestBody)
    suspend fun myshiftchangerequestlist(authtoken: String) = apiInterface.myshiftchangerequestlist(authtoken)



}