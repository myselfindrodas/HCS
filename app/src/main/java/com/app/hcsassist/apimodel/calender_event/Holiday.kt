package com.app.hcsassist.apimodel.calender_event


import com.google.gson.annotations.SerializedName

data class Holiday(
    @SerializedName("company_id")
    val companyId: Int?,
    @SerializedName("created_at")
    val createdAt: Any?,
    @SerializedName("created_by")
    val createdBy: Int?,
    @SerializedName("end_date")
    val endDate: String?,
    @SerializedName("holiday")
    val holiday: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("location_id")
    val locationId: Int?,
    @SerializedName("start_date")
    val startDate: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("updated_at")
    val updatedAt: Any?
)