package com.nudriin.fits.data.domain

import com.google.gson.annotations.SerializedName

data class Grade(

    @field:SerializedName("grade_desc")
    val gradeDesc: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("grade_name")
    val gradeName: String
)