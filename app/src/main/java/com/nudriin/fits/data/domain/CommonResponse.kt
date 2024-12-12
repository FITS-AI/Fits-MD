package com.nudriin.fits.data.domain

import com.google.gson.annotations.SerializedName

data class CommonResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String
    
)