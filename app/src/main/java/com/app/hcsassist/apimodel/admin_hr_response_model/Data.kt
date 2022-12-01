package com.app.hcsassist.apimodel.admin_hr_response_model


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("contract")
    val contract: Contract?,
    @SerializedName("permanent")
    val permanent: Permanent?,
    @SerializedName("thirdparty")
    val thirdparty: Thirdparty?
)