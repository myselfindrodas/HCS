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
    val full_address: full_address? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("profile_image")
    val profile_image: String? = null,

    @field:SerializedName("snapshot")
    val snapshot: String? = null,

    @field:SerializedName("user_type_id")
    val user_type_id: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("token")
    val token: String? = null,

)

data class full_address(
    @field:SerializedName("present")
    val present: present? = null,

    @field:SerializedName("permanent")
    val permanent: permanent? = null,
)

data class present(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
)


data class permanent(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
)