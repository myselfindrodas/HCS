package com.app.hcsassist.apimodel.mss_attendance


import com.google.gson.annotations.SerializedName

data class MSSAttendance_ResponseModel(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)