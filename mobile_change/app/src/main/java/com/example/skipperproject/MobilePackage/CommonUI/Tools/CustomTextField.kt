package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperDimensions

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Используем BasicTextField для полного контроля над высотой и центровкой
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp) // Ваша фиксированная высота
            .background(
                color = Color.White,
                shape = RoundedCornerShape(SkipperDimensions.inputFieldRound)
            ),
        textStyle = MobileTextStyles.HintText.copy(color = Color.Black),
        singleLine = true,
        cursorBrush = SolidColor(Color.Black),
        decorationBox = { innerTextField ->
            // Этот контейнер отвечает за центровку текста внутри 42.dp
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Горизонтальный отступ как в оригинале
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    // Здесь можно добавить placeholder, если он понадобится
                }
                innerTextField()
            }
        }
    )
}
