package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @field:SerializedName("username")
    val username: String? = "",

    @field:SerializedName("password")
    val password: String? = "",

    @field:SerializedName("lat")
    val lat: String? = "",

    @field:SerializedName("long")
    val long: String? = "",

    @field:SerializedName("device_type")
    val device_type: String? = "",

    @field:SerializedName("device_id")
    val device_id: String? = "",
)