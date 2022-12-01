package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class LogoutRequest(

    @field:SerializedName("logout_lat")
    val logout_lat: String? = "",

    @field:SerializedName("logout_long")
    val logout_long: String? = "",

)