package com.app.hcsassist.apimodel.admin_hr_response_model

import com.google.gson.annotations.SerializedName

data class AdminHrListRequest(
    @field:SerializedName("location_id")
    val location_id: Int? = 0,
    @field:SerializedName("date")
    val date: String? = ""
)