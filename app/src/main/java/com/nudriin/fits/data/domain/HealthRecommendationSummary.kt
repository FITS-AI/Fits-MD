package com.nudriin.fits.data.domain

data class HealthRecommendationSummary(
    val grade: String,
    val overall: String,
    val sugar: String,
    val fat: String,
    val protein: String,
    val calories: String,
    val warning: String
)