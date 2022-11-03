package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,


    @field:SerializedName("data")
    val data: dataclass? = null,


    @field:SerializedName("errors")
    val errors: String? = null
)

data class dataclass(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("email_verified_at")
    val email_verified_at: String? = null,

    @field:SerializedName("usercode")
    val usercode: String? = null,

    @field:SerializedName("full_address")
    val full_address: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("user_type_id")
    val user_type_id: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

)