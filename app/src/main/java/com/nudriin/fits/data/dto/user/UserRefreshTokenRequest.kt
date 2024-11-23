package com.nudriin.fits.data.dto.user

import com.google.gson.annotations.SerializedName

data class UserRefreshTokenRequest(

	@field:SerializedName("refreshToken")
	val refreshToken: String
)
