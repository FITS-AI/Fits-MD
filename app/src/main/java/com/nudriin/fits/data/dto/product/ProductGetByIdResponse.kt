package com.nudriin.fits.data.dto.product

import com.google.gson.annotations.SerializedName
import com.nudriin.fits.data.domain.Grade

data class ProductGetByIdResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("productDetails")
    val productDetails: List<ProductDetailsItem>
)

data class ProductAllergenItem(

    @field:SerializedName("allergen")
    val allergen: String,

    @field:SerializedName("product_id")
    val productId: String,

    @field:SerializedName("allergy_id")
    val allergyId: Int,

    @field:SerializedName("allergy")
    val allergy: Allergy,

    @field:SerializedName("id")
    val id: String
)

data class ProductDetailsItem(

    @field:SerializedName("fiber_ing")
    val fiberIng: String,

    @field:SerializedName("carbo")
    val carbo: String,

    @field:SerializedName("fiber")
    val fiber: String,

    @field:SerializedName("product_allergen")
    val productAllergen: List<ProductAllergenItem>,

    @field:SerializedName("grades_id")
    val gradesId: Int,

    @field:SerializedName("calories")
    val calories: String,

    @field:SerializedName("fat_ing")
    val fatIng: String,

    @field:SerializedName("history_product")
    val historyProduct: List<HistoryProductItem>,

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
    val sugar: String,

    @field:SerializedName("overall")
    val overall: String,

    @field:SerializedName("health_assessment")
    val healthAssessment: String,
)

data class Allergy(

    @field:SerializedName("allergen")
    val allergen: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("type")
    val type: String,

    @field:SerializedName("allergy_name")
    val allergyName: String,

    @field:SerializedName("class")
    val jsonMemberClass: String,

    @field:SerializedName("group")
    val group: String
)

data class HistoryProductItem(

    @field:SerializedName("product_id")
    val productId: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("users_id")
    val usersId: String,

    @field:SerializedName("id")
    val id: String
)
