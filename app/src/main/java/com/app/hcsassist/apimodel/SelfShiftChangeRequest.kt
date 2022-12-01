package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class SelfShiftChangeRequest(
    @field:SerializedName("shift_id")
    val shift_id: String? = "",

    @field:SerializedName("comment")
    val comment: String? = "",
)