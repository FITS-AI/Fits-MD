package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyUserSaveResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)
