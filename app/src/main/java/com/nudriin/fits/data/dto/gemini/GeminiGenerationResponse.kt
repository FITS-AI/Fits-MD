package com.nudriin.fits.data.dto.gemini

import com.google.gson.annotations.SerializedName

data class GeminiGenerationResponse(
    @field:SerializedName("analysis")
    val analysis: String,

    @field:SerializedName("grade")
    val grade: String,

    @field:SerializedName("sugar")
    val sugar: String,

    @field:SerializedName("sugar_ing")
    val sugarIng: String,

    @field:SerializedName("calories")
    val calories: String,

    @field:SerializedName("calories_ing")
    val caloriesIng: String,

    @field:SerializedName("fat")
    val fat: String,

    @field:SerializedName("fat_ing")
    val fatIng: String,

    @field:SerializedName("protein")
    val protein: String,

    @field:SerializedName("protein_ing")
    val proteinIng: String,
)
