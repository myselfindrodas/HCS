package com.app.hcsassist.apimodel.myattendance


import com.google.gson.annotations.SerializedName

data class CurrentShift(
    @SerializedName("end_time")
    val endTime: String?,
    @SerializedName("shift_title")
    val shiftTitle: String?,
    @SerializedName("start_time")
    val startTime: String?
)