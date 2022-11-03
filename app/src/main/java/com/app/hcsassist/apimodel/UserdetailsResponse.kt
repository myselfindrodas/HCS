package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class UserdetailsResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: data? = null
)

data class data(
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

    @field:SerializedName("profile_image")
    val profile_image: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null,

    @field:SerializedName("reporting_manager")
    val reporting_manager: reporting_manager? = null
)

data class reporting_manager(

    @field:SerializedName("user")
    val user: userdata? = null
)

data class userdata(

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("last_name")
    val last_name: String? = null,

    @field:SerializedName("full_address")
    val full_address: String? = null,
)