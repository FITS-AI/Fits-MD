package com.nudriin.fits.data.dto.product

data class ProductGetAllResponse(
    val success: Boolean,
    val userHistory: List<UserHistoryItem>,
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
    val product: Product,
    val productId: String,
    val createdAt: String,
    val usersId: String,
    val id: String
)

data class Grade(
    val gradeDesc: String,
    val id: Int,
    val gradeName: String
)

data class Product(
    val fiberIng: String,
    val carbo: String,
    val fiber: String,
    val productAllergen: List<Any>,
    val gradesId: Int,
    val calories: String,
    val fatIng: String,
    val proteinIng: String,
    val protein: String,
    val carboIng: String,
    val grade: Grade,
    val name: String,
    val caloriesIng: String,
    val fat: String,
    val id: String,
    val sugarIng: String,
    val sugar: String
)
