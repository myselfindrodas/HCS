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

    @field:SerializedName("last_name")
    val last_name: String? = null,

    @field:SerializedName("email")
    val email: String? = null,

    @field:SerializedName("email_verified_at")
    val email_verified_at: String? = null,

    @field:SerializedName("usercode")
    val usercode: String? = null,

    @field:SerializedName("full_address")
    val full_address: fulladdress? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("phone")
    val phone: String? = null,

    @field:SerializedName("snapshot")
    val snapshot: String? = null,

    @field:SerializedName("profile_image")
    val profile_image: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("updated_at")
    val updated_at: String? = null,

    @field:SerializedName("locations")
    val locations: String? = null,

    @field:SerializedName("attendance_status")
    val attendance_status: Int? = null,

    @field:SerializedName("punch_in_id")
    val punch_in_id: Int? = null,

    @field:SerializedName("reporting_manager")
    val reporting_manager: reporting_manager? = null,

    @field:SerializedName("punch_in_loc")
    val punch_in_loc: String? = null,

    @field:SerializedName("punch_in_time")
    val punch_in_time: String? = null,

    @field:SerializedName("is_on_leave")
    val is_on_leave: String? = null,

    @field:SerializedName("is_holiday")
    val is_holiday: String? = null,

    @field:SerializedName("punched_out_shift")
    val punched_out_shift: List<String?>? = null,

    @field:SerializedName("default_shift")
    val default_shift: defaultshift? = null,

    @field:SerializedName("user_type")
    val user_type: user_type? = null,
)

data class fulladdress(
    @field:SerializedName("present")
    val present: present_address? = null,

    @field:SerializedName("permanent")
    val permanent: permanent_address? = null,
)

data class user_type(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("user_type_name")
    val user_type_name: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("status")
    val status: String? = null,
)

data class defaultshift(

    @field:SerializedName("shift_title")
    val shift_title: String? = null,
)

data class present_address(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
)


data class permanent_address(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
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
    val full_address: fulladdress? = null,
)