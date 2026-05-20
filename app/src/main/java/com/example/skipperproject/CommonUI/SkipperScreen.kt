package com.example.skipperproject.CommonUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skipperproject.ui.theme.MobileTextStyles
import com.example.skipperproject.ui.theme.SkipperColors
import com.example.skipperproject.ui.theme.SkipperDimensions

@Composable
fun SkipperScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Верхняя желтая полоса
        DecorativeBar()

        // Основной контент
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }

        // Нижняя желтая полоса
        DecorativeBar()
    }
}

@Composable
private fun DecorativeBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(SkipperColors.mainYellow)
    )
}


