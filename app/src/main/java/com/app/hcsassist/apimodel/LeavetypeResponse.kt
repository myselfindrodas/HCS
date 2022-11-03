package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class LeavetypeResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val result: List<DataList?>? = null,

    )

data class DataList(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("company_id")
    val company_id: String? = null,

    @field:SerializedName("emp_type_id")
    val emp_type_id: String? = null,

    @field:SerializedName("leave_type")
    val leave_type: String? = null,

    @field:SerializedName("location_id")
    val location_id: String? = null,

    @field:SerializedName("no_of_leave")
    val no_of_leave: String? = null,

    @field:SerializedName("leave_type_code")
    val leave_type_code: String? = null,

    @field:SerializedName("short_code")
    val short_code: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

)