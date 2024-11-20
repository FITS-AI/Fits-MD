package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserLoginRequest(

	@field:SerializedName("password")
	val password: String,

	@field:SerializedName("email")
	val email: String
)
