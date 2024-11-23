package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserSaveResponse(

    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("accessToken")
    val accessToken: String? = null,

    @field:SerializedName("refreshToken")
    val refreshToken: String? = null
)
