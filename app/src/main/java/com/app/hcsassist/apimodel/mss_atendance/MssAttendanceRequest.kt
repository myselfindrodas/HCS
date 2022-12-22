package com.app.hcsassist.apimodel.myattendance

import com.google.gson.annotations.SerializedName

data class MssAttendanceRequest(
    @field:SerializedName("attendence_date")
    val attendenceDate: String? = "",

    @field:SerializedName("page")
    val page: String? = "",
)