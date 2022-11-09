package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class PunchoutResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

)
