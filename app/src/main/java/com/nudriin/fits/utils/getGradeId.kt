package com.nudriin.fits.utils

fun getGradeId(grade: String): Int? {
    return when (grade) {
        "A" -> 1
        "B" -> 2
        "C" -> 3
        "D" -> 4
        else -> null
    }
}