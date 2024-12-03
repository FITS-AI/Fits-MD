package com.nudriin.fits.data.dto.allergy

import com.google.gson.annotations.SerializedName

data class AllergyGetAllResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("allergy")
    val allergy: List<AllergyItem>,

    @field:SerializedName("message")
    val message: String
)

data class AllergyItem(

    @field:SerializedName("allergen")
    val allergen: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("allergy_name")
    val allergyName: String,

    var isSelected: Boolean = false
)
