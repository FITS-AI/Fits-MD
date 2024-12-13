package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyUserSaveRequest(
    @SerializedName("data")
    val data: List<AllergyId>
)

data class AllergyId(
    @field:SerializedName("id")
    val id: Int
)
