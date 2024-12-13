package com.nudriin.fits.data.dto.gemini

import com.google.gson.annotations.SerializedName

data class GeminiResponse(

    @field:SerializedName("candidates")
    val candidates: List<CandidatesItem>,

    @field:SerializedName("modelVersion")
    val modelVersion: String,

    @field:SerializedName("usageMetadata")
    val usageMetadata: UsageMetadata
)

data class UsageMetadata(

    @field:SerializedName("candidatesTokenCount")
    val candidatesTokenCount: Int,

    @field:SerializedName("totalTokenCount")
    val totalTokenCount: Int,

    @field:SerializedName("promptTokenCount")
    val promptTokenCount: Int
)

data class CandidatesItem(

    @field:SerializedName("avgLogprobs")
    val avgLogprobs: Any,

    @field:SerializedName("finishReason")
    val finishReason: String,

    @field:SerializedName("content")
    val content: DataContent
)

data class DataContent(

    @field:SerializedName("role")
    val role: String,

    @field:SerializedName("parts")
    val parts: List<PartsItem>
)

data class PartsItem(

    @field:SerializedName("text")
    val text: String
)
