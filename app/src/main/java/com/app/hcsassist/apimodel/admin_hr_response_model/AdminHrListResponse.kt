package com.app.hcsassist.apimodel.admin_hr_response_model


import com.google.gson.annotations.SerializedName

data class AdminHrListResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)