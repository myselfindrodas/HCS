package com.app.hcsassist.retrofit

import com.app.hcsassist.apimodel.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ApiHelper(private val apiInterface: ApiInterface) {

    suspend fun login(appKey: String, requestBody: LoginRequest) = apiInterface.login(appKey,requestBody)
    suspend fun userdetails(authtoken: String) = apiInterface.myprofile(authtoken)
    suspend fun picupload(authtoken: String, part: MultipartBody.Part) = apiInterface.uploadImage(authtoken, part)
    suspend fun logout(authtoken: String) = apiInterface.logout(authtoken)
    suspend fun changepassword(authtoken: String, requestBody:ChangePasswordRequest) = apiInterface.changepassword(authtoken, requestBody)
    suspend fun shiftlist(authtoken: String) = apiInterface.shiftlist(authtoken)
    suspend fun shiftchangerequest(authtoken: String, requestBody: ShiftChangeRequest) = apiInterface.shiftchangerequest(authtoken, requestBody)
    suspend fun shiftchangelist(authtoken: String) = apiInterface.shiftchangelist(authtoken)
    suspend fun shiftchangeapproval(authtoken: String, requestBody: ShiftApprovalRequest) = apiInterface.shiftchangeapproval(authtoken, requestBody)
    suspend fun leavetype(authtoken: String) = apiInterface.leavetype(authtoken)
    suspend fun leaveapply(authtoken: String,
                           leave_type_id: RequestBody,
                           leave_date_from: RequestBody,
                           leave_date_to:RequestBody,
                           comment:RequestBody,
                           part: MultipartBody.Part) =
        apiInterface.leaveapply(authtoken,
            leave_type_id,
            leave_date_from,
            leave_date_to,
            comment,
            part)


    suspend fun leavelist(authtoken: String) = apiInterface.myleave(authtoken)




}