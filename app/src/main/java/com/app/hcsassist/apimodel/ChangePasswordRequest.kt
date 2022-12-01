package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ChangePasswordRequest(
    @field:SerializedName("password")
    val password: String? = "",

    @field:SerializedName("password_confirmation")
    val password_confirmation: String? = ""
)