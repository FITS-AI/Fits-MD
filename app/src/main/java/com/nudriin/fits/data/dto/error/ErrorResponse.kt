package com.nudriin.fits.data.dto.error

import com.google.gson.annotations.SerializedName

data class ErrorResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
