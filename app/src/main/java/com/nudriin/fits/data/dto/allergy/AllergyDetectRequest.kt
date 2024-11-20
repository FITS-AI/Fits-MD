package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyDetectRequest(

	@field:SerializedName("ingredients")
	val ingredients: String
)
