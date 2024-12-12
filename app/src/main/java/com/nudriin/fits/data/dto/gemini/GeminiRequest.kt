package com.nudriin.fits.data.dto.gemini

data class GeminiRequest(
    val contents: List<Contents>,
)

data class Contents(
    val parts: List<Part>,
)

data class Part(
    val text: String,
)
