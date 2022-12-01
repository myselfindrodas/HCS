package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ShiftListResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val result: List<DataItem?>? = null,

)


data class DataItem(

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