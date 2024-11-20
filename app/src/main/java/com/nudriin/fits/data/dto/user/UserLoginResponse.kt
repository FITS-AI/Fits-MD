package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserLoginResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("user")
    val user: User,

    @field:SerializedName("refreshToken")
    val refreshToken: String
)
