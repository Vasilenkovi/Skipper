package com.example.skipperproject.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.example.skipperproject.R

// 1. Сначала создаем FontFamily, объединяя ваши файлы шрифтов
val SegoeUI = FontFamily(
    Font(R.font.segoe_ui_regular, FontWeight.Normal),
    Font(R.font.segoe_ui_bold, FontWeight.Bold),
    Font(R.font.segoe_ui_semi_bold, FontWeight.SemiBold)
)

object MobileTextStyles{
    val MainScreenText = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-2).sp
    )

    val MainScreenButton = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 46.sp,
        letterSpacing = (-2).sp
    )

    val ButtonsText = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    )

    val HintText = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 46 .sp,
        letterSpacing = 0.sp
    )

//    val HintTextUnderlined = HintText.copy(
//        textDecoration = TextDecoration.Underline
//    )

    val HelpButtonSmall = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    )

    val HelpButtonSmallUnderlined = HelpButtonSmall.copy(
        textDecoration = TextDecoration.Underline
    )

    val HelpButtonLarge = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    )

    val HelpButtonLargeUnderlined = HelpButtonLarge.copy(
        textDecoration = TextDecoration.Underline
    )

    val QuestionText = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    )

    val SmallestText = TextStyle(
        fontFamily = SegoeUI,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 46.sp,
        letterSpacing = 0.sp
    )

}
