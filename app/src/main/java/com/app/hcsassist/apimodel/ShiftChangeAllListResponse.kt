package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ShiftChangeAllListResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val result: List<Listarray?>? = null,

    )


data class Listarray(

    @field:SerializedName("comment")
    val comment: String? = null,

    @field:SerializedName("company_name")
    val company_name: String? = null,

    @field:SerializedName("reporting_manager_name")
    val reporting_manager_name: String? = null,


    @field:SerializedName("shift_title")
    val shift_title: String? = null,

    @field:SerializedName("created_at")
    val created_at: String? = null,

    @field:SerializedName("date_from")
    val date_from: String? = null,

    )