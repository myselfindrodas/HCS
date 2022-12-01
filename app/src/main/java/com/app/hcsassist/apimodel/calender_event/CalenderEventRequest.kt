package com.app.hcsassist.apimodel.myattendance

import com.google.gson.annotations.SerializedName

data class CalenderEventRequest(
    @field:SerializedName("date")
    val date: String? = ""
)