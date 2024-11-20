package com.nudriin.fits.data.dto.product

import com.google.gson.annotations.SerializedName

data class ProductGetByIdResponse(

    @field:SerializedName("usersHistory")
    val usersHistory: UsersHistory,

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class AllergyItemGetIdByResponse(

    @field:SerializedName("allergen")
    val allergen: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("allergy_name")
    val allergyName: String
)

data class UsersHistory(

    @field:SerializedName("product")
    val product: List<ProductItem>,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("users_id")
    val usersId: String,

    @field:SerializedName("id")
    val id: String
)

data class ProductItem(

    @field:SerializedName("fiber_ing")
    val fiberIng: String,

    @field:SerializedName("carbo")
    val carbo: String,

    @field:SerializedName("fiber")
    val fiber: String,

    @field:SerializedName("allergy")
    val allergy: List<AllergyItemGetIdByResponse>,

    @field:SerializedName("calories")
    val calories: String,

    @field:SerializedName("fat_ing")
    val fatIng: String,

    @field:SerializedName("protein_ing")
    val proteinIng: String,

    @field:SerializedName("grade")
    val grade: Grade,

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

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("sugar_ing")
    val sugarIng: String,

    @field:SerializedName("sugar")
    val sugar: String
)
