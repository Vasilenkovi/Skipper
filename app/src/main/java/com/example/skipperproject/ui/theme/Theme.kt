package com.example.skipperproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//private val LightColorScheme = lightColorScheme(
//    primary = SkipperColors.white7
//    secondary = Color(0xFFBCBCBC),
//    tertiary = Color(0xFFEBEBEB),
//    background = Color.White,
//    surface = Color.White,
//    onPrimary = Color.Black,
//    onSecondary = Color.Black,
//    onTertiary = Color.Black,
//    onBackground = Color.Black,
//    onSurface = Color.Black
//)

// Настраиваем стандартную типографику Material 3, используя ваши стили из MobileTextStyles
private val AppTypography = Typography(
    displayLarge = MobileTextStyles.MainScreenText,
    headlineLarge = MobileTextStyles.MainScreenText,
    labelLarge = MobileTextStyles.ButtonsText,
    labelMedium = MobileTextStyles.MainScreenButton,
    bodyLarge = MobileTextStyles.HintText,
    bodyMedium = MobileTextStyles.QuestionText,
    bodySmall = MobileTextStyles.HelpButtonSmallUnderlined,
    titleMedium = MobileTextStyles.HelpButtonLargeUnderlined
)

@Composable
fun SkipperProjectTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = AppTypography,
        content = content
    )
}
