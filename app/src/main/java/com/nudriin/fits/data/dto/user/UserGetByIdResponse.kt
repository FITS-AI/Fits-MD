package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserGetByIdResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: User
)

data class User(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("email")
    val email: String
)
