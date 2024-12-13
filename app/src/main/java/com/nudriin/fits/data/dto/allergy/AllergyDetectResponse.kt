package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyDetectResponse(

	@field:SerializedName("allergyContained")
	val allergyContained: List<AllergyContainedItem>,

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class AllergyContainedItem(

	@field:SerializedName("allergen")
	val allergen: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("allergy_name")
	val allergyName: String
)
