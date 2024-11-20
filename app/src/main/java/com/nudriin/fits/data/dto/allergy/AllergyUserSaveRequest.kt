package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyUserSaveRequest(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("allergy_name")
	val allergyName: String
)
