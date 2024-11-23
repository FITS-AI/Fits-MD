package com.nudriin.fits.data.dto.article

import com.google.gson.annotations.SerializedName

data class ArticleGetAllResponse(

	@field:SerializedName("success")
	val success: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("article")
	val article: List<ArticleItem>
)

data class ArticleItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("author")
	val author: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String
)
