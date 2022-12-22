package com.app.hcsassist.apimodel.mss_attendance


import com.app.hcsassist.apimodel.pagination
import com.google.gson.annotations.SerializedName

data class MSSAttendance_ResponseModel(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @field:SerializedName("pagination")
    val attendancepagination: attendancepagination? = null,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: Boolean?
)

data class attendancepagination(
    @field:SerializedName("current_page")
    val current_page: String? = null,

    @field:SerializedName("next_page")
    val next_page: String? = null,

    @field:SerializedName("total_pages")
    val total_pages: String? = null,
)