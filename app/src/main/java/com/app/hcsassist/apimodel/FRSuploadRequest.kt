package com.app.hcsassist.apimodel

import com.google.gson.annotations.SerializedName

data class FRSuploadRequest(
    @field:SerializedName("image")
    val image: String? = "",

)