package com.app.hcsassist.apimodel.myattendance

import com.google.gson.annotations.SerializedName

data class MyAttendanceRequest(
    @field:SerializedName("date")
    val date: String? = ""
)