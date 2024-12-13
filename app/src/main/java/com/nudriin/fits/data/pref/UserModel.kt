package com.nudriin.fits.data.pref

data class UserModel(
    val id: String,
    val name: String,
    val email: String,
    val token: String,
    val refreshToken: String,
    val isLogin: Boolean = false
)