package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyDeleteRequest(

	@field:SerializedName("id")
	val id: List<Int>
)
