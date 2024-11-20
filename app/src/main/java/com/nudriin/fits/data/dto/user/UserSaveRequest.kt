package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserSaveRequest(

    @field:SerializedName("password")
    val password: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("email")
    val email: String
)
