package com.app.hcsassist.apimodel.mss_attendance


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("attendance")
    val attendance: String?,
    @SerializedName("emp_type_name")
    val empTypeName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("profile_image")
    val profileImage: String?
)