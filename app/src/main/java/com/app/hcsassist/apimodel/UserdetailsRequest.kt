package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class UserdetailsRequest(
    @field:SerializedName("Accept")
    val Accept: String? = "",

    @field:SerializedName("Authorization")
    val Authorization: String? = ""
)