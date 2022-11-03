package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class ShiftChangeRequest(
    @field:SerializedName("shift_id")
    val shift_id: String? = "",

    @field:SerializedName("date_from")
    val date_from: String? = "",

    @field:SerializedName("comment")
    val comment: String? = ""
)