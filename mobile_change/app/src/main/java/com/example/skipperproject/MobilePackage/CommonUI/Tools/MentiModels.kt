package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.ui.graphics.painter.Painter

data class Menti(
    val name: String,
    val photo: Painter? = null
)

data class MentiLesson(
    val title: String,
    val mentorName: String,
    val date: String,
    val time: String,
    val isPast: Boolean = false
)
