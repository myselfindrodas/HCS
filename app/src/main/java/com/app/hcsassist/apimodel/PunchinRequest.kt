package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class PunchinRequest(
    @field:SerializedName("punch_in_location")
    val punch_in_location: String? = "",
    @field:SerializedName("device_type")
    val device_type: String? = "",
    @field:SerializedName("imei_no")
    val imei_no: String? = "",
    @field:SerializedName("device_info")
    val device_info: String? = "",
)