package com.example.wemu.repository

import com.app.hcsassist.apimodel.*
import com.app.hcsassist.retrofit.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainRepository(private val apiHelper: ApiHelper) {

    suspend fun login(appKey: String, requestBody: LoginRequest) = apiHelper.login(appKey,requestBody)
    suspend fun userdetails(authtoken: String) = apiHelper.userdetails(authtoken)
    suspend fun picupload(authtoken: String, part: MultipartBody.Part) = apiHelper.picupload(authtoken, part)
    suspend fun logout(authtoken: String) = apiHelper.logout(authtoken)
    suspend fun changepassword(authtoken: String, requestBody: ChangePasswordRequest) = apiHelper.changepassword(authtoken, requestBody)
    suspend fun shiftlist(authtoken: String) = apiHelper.shiftlist(authtoken)
    suspend fun shiftchangerequest(authtoken: String, requestBody: ShiftChangeRequest) = apiHelper.shiftchangerequest(authtoken, requestBody)
    suspend fun shiftchangelist(authtoken: String) = apiHelper.shiftchangelist(authtoken)
    suspend fun shiftchangeapproval(authtoken: String, requestBody: ShiftApprovalRequest) = apiHelper.shiftchangeapproval(authtoken, requestBody)
    suspend fun leavetype(authtoken: String) = apiHelper.leavetype(authtoken)
    suspend fun leaveapply(authtoken: String,
                           leave_type_id: RequestBody,
                           leave_date_from: RequestBody,
                           leave_date_to:RequestBody,
                           comment:RequestBody,
                           part: MultipartBody.Part) =
        apiHelper.leaveapply(authtoken,
            leave_type_id,
            leave_date_from,
            leave_date_to,
            comment,
            part)

    suspend fun leavelist(authtoken: String) = apiHelper.leavelist(authtoken)


}