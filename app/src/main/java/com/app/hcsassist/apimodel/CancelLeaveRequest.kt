package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class CancelLeaveRequest(
    @field:SerializedName("request_id")
    val request_id: String? = "",

    @field:SerializedName("status")
    val status: String? = "",
)