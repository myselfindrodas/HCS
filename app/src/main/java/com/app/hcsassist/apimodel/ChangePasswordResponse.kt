package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("errors")
    val errors: String? = null,

)