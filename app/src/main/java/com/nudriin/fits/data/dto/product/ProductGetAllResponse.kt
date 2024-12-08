package com.nudriin.fits.data.dto.product

import com.google.gson.annotations.SerializedName

data class ProductGetAllResponse(
    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("userHistory")
    val userHistory: List<UserHistoryItem>,

    @field:SerializedName("message")
    val message: String
)

data class ProductAllergy(
    val allergen: String,
    val id: Int,
    val type: String,
    val allergyName: String,
    val jsonMemberClass: String,
    val group: String
)

data class UserHistoryItem(
    @field:SerializedName("product")
    val product: Product,

    @field:SerializedName("product_id")
    val productId: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("users_id")
    val usersId: String,

    @field:SerializedName("id")
    val id: String
)

data class Grade(
    @field:SerializedName("grade_desc")
    val gradeDesc: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("grade_name")
    val gradeName: String
)

data class Product(
    @field:SerializedName("fiber_ing")
    val fiberIng: String,

    @field:SerializedName("carbo")
    val carbo: String,

    @field:SerializedName("fiber")
    val fiber: String,

    @field:SerializedName("product_allergen")
    val productAllergen: List<Any>,

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

    @field:SerializedName("grade")
    val grade: Grade,

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
