package com.app.hcsassist.apimodel.myattendance


import com.app.hcsassist.apimodel.permanent_address
import com.app.hcsassist.apimodel.present_address
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("company_id")
    val companyId: Int?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String?,
    @SerializedName("emp_type_id")
    val empTypeId: Int?,
    @SerializedName("full_address")
    val fullAddress: fullAddress?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("nick_name")
    val nickName: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("profile_image")
    val profileImage: String?,
    @SerializedName("snapshot")
    val snapshot: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("user_type_id")
    val userTypeId: Int?,
    @SerializedName("usercode")
    val usercode: String?
)

data class fullAddress(
    @field:SerializedName("present")
    val present: presentaddress? = null,

    @field:SerializedName("permanent")
    val permanent: permanentaddress? = null,
)

data class presentaddress(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
)


data class permanentaddress(
    @field:SerializedName("address_line_1")
    val address_line_1: String? = null,

    @field:SerializedName("address_line_2")
    val address_line_2: String? = null,

    @field:SerializedName("city")
    val city: String? = null,

    @field:SerializedName("postal_code")
    val postal_code: String? = null,
)