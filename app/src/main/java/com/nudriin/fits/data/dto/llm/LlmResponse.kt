package com.nudriin.fits.data.dto.llm

import com.google.gson.annotations.SerializedName

data class LlmResponse(

	@field:SerializedName("data")
	val data: String,

	@field:SerializedName("success")
	val success: Boolean
)
