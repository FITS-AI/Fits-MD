package com.nudriin.fits.data.dto.product

import com.google.gson.annotations.SerializedName

data class ProductSaveRequest(

	@field:SerializedName("fiber_ing")
	val fiberIng: String,

	@field:SerializedName("carbo")
	val carbo: String,

	@field:SerializedName("fiber")
	val fiber: String,

	@field:SerializedName("allergy")
	val allergy: List<AllergyItem>,

	@field:SerializedName("grades_id")
	val gradesId: Int,

	@field:SerializedName("calories")
	val calories: String,

	@field:SerializedName("fat_ing")
	val fatIng: String,

	@field:SerializedName("protein_ing")
	val proteinIng: String,

	@field:SerializedName("protein")
	val protein: String,

	@field:SerializedName("carbo_ing")
	val carboIng: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("calories_ing")
	val caloriesIng: String,

	@field:SerializedName("fat")
	val fat: String,

	@field:SerializedName("sugar_ing")
	val sugarIng: String,

	@field:SerializedName("sugar")
	val sugar: String
)

data class AllergyItem(

	@field:SerializedName("allergy_id")
	val allergyId: Int
)
