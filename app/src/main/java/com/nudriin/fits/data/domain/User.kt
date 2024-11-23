package com.nudriin.fits.data.domain

import com.google.gson.annotations.SerializedName

data class User(

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("email")
    val email: String
)