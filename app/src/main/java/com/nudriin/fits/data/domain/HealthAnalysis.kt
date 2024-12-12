package com.nudriin.fits.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HealthAnalysis(
    val name: String,
    val description: String
) : Parcelable