package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserGetByIdResponse(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("user")
    val user: List<UserItem>
)

data class UsersAllergyItem(

    @field:SerializedName("users_id")
    val usersId: String,

    @field:SerializedName("allergy_id")
    val allergyId: Int,

    @field:SerializedName("allergy")
    val allergy: Allergy,

    @field:SerializedName("id")
    val id: String
)

data class UserItem(

    @field:SerializedName("refresh_token")
    val refreshToken: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("created_at")
    val createdAt: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("users_allergy")
    val usersAllergy: List<UsersAllergyItem>,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("token")
    val token: String
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
