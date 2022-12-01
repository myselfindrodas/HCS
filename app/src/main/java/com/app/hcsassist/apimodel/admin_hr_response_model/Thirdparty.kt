package com.app.hcsassist.apimodel.admin_hr_response_model


import com.google.gson.annotations.SerializedName

data class Thirdparty(
    @SerializedName("absent")
    val absent: Int?=0,
    @SerializedName("present")
    val present: Int?=0,
    @SerializedName("totalEmployee")
    val totalEmployee: Int?=0
)