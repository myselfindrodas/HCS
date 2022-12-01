package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class FRSRequest(
    @field:SerializedName("snapshot_image")
    val snapshot_image: String? = "",

    @field:SerializedName("verification_image")
    val verification_image: String? = "",

    @field:SerializedName("employee_id")
    val employee_id: String? = "",
)