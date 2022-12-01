package com.app.hcsassist.apimodel.calender_event


import com.google.gson.annotations.SerializedName

data class CalenderEventResponseModel(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)