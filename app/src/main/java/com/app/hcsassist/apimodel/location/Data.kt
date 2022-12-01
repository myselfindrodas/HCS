package com.app.hcsassist.apimodel.location


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    val address: String?,
    @SerializedName("city_id")
    val cityId: Int?,
    @SerializedName("company_id")
    val companyId: Int?,
    @SerializedName("country_id")
    val countryId: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("deleted_at")
    val deletedAt: Any?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("location_name")
    val locationName: String?,
    @SerializedName("long")
    val long: String?,
    @SerializedName("state_id")
    val stateId: Int?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("updated_at")
    val updatedAt: Any?,
    @SerializedName("zip")
    val zip: String?
)