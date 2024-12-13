package com.nudriin.fits.data.dto.llm

import com.google.gson.annotations.SerializedName

data class LlmRequest(

	@field:SerializedName("prompt")
	val prompt: String
)
