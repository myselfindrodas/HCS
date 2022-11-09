package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class PunchinResponse(
    @field:SerializedName("status")
    val status: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("data")
    val data: data_class? = null,
)

data class data_class(

    @field:SerializedName("id")
    val id: String? = null,
)