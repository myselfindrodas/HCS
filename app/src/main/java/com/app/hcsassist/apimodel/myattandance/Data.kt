package com.app.hcsassist.apimodel.myattendance


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("attendance_date")
    val attendanceDate: String?,
    @SerializedName("attendence_status")
    val attendenceStatus: Int?,
    @SerializedName("company_id")
    val companyId: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("currentShift")
    val currentShift: CurrentShift?,
    @SerializedName("day")
    val day: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("punch_in_location")
    val punchInLocation: String?,
    @SerializedName("punch_in_time")
    val punchInTime: String?,
    @SerializedName("punch_out_location")
    val punchOutLocation: String?,
    @SerializedName("punch_out_time")
    val punchOutTime: String?,
    @SerializedName("total_dutarion")
    val totalDutarion: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user")
    val user: User?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("reason")
    val reason: String?,
    @SerializedName("user_id")
    val userId: Int?
)