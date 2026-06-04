package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.ui.graphics.painter.Painter

data class Mentor(
    val name: String,
    val description: String,
    val photo: Painter? = null
)

data class Course(
    val title: String,
    val description: String,
    val price: String
)

data class Review(
    val name: String,
    val text: String,
    val rating: Int // 1 to 5
)
