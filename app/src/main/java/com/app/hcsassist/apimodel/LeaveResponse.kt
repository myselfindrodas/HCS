package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class LeaveResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,


    @field:SerializedName("data")
    val result: List<data2?>? = null,

)

data class data2(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("leave_type_id")
    val leave_type_id: String? = null,

    @field:SerializedName("user_id")
    val user_id: String? = null,

    @field:SerializedName("notified_user_id")
    val notified_user_id: String? = null,

    @field:SerializedName("approved_status")
    val approved_status: String? = null,

    @field:SerializedName("leave_date_from")
    val leave_date_from: String? = null,

    @field:SerializedName("leave_date_to")
    val leave_date_to: String? = null,

    @field:SerializedName("applied_on")
    val applied_on: String? = null,

    @field:SerializedName("comment")
    val comment: String? = null,

    @field:SerializedName("attachment")
    val attachment: String? = null,

    @field:SerializedName("app_reject_on")
    val app_reject_on: String? = null,


    @field:SerializedName("user")
    val data: userclass? = null,

    @field:SerializedName("leave_type")
    val leavetype: leavetype? = null,


    )

data class userclass(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("last_name")
    val last_name: String? = null,

    @field:SerializedName("nick_name")
    val nick_name: String? = null,

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

    @field:SerializedName("user_type_id")
    val user_type_id: String? = null,

    @field:SerializedName("emp_type_id")
    val emp_type_id: String? = null,

    @field:SerializedName("full_profile_image")
    val full_profile_image: String? = null,
)


data class leavetype(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("emp_type_id")
    val emp_type_id: String? = null,

    @field:SerializedName("location_id")
    val location_id: String? = null,

    @field:SerializedName("leave_type")
    val leave_type: String? = null,

    @field:SerializedName("short_code")
    val short_code: String? = null,

    @field:SerializedName("no_of_leave")
    val no_of_leave: String? = null,

    @field:SerializedName("leave_type_code")
    val leave_type_code: String? = null,

    @field:SerializedName("status")
    val status: String? = null,
)