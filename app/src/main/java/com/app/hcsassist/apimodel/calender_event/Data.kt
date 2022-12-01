package com.app.hcsassist.apimodel.calender_event


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("events")
    val events: List<Event?>?,
    @SerializedName("holidays")
    val holidays: List<Holiday?>?
)