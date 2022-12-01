package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class PunchoutRequest(
    @field:SerializedName("punch_out_location")
    val punch_out_location: String? = null,

)