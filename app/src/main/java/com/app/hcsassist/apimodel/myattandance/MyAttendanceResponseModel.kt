package com.app.hcsassist.apimodel.myattendance


import com.google.gson.annotations.SerializedName

data class MyAttendanceResponseModel(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)