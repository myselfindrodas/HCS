package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ShiftChangeListResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val result: List<DataArray?>? = null,

    )

data class DataArray(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("user_id")
    val user_id: String? = null,

    @field:SerializedName("shift_id")
    val shift_id: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("reporting_manager_id")
    val reporting_manager_id: String? = null,

    @field:SerializedName("date_from")
    val date_from: String? = null,

    @field:SerializedName("date_to")
    val date_to: String? = null,

    @field:SerializedName("approved_by")
    val approved_by: String? = null,


    @field:SerializedName("comment")
    val comment: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("currentShift")
    val currentShift: currentShift? = null,

    @field:SerializedName("user")
    val user: user? = null,

    @field:SerializedName("shift")
    val shift: shift? = null


)

data class currentShift(
    @field:SerializedName("end_time")
    val end_time: String? = null,

    @field:SerializedName("shift_title")
    val shift_title: String? = null,

    @field:SerializedName("start_time")
    val start_time: String? = null,

    )

data class user(

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
    val full_address: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("profile_image")
    val profile_image: String? = null,

    @field:SerializedName("user_type_id")
    val user_type_id: String? = null,

    @field:SerializedName("emp_type_id")
    val emp_type_id: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null,

    @field:SerializedName("full_profile_image")
    val full_profile_image: String? = null,
)

data class shift(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("location_id")
    val location_id: String? = null,

    @field:SerializedName("shift_title")
    val shift_title: String? = null,

    @field:SerializedName("start_time")
    val start_time: String? = null,

    @field:SerializedName("end_time")
    val end_time: String? = null,

    @field:SerializedName("duration")
    val duration: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

)