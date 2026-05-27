package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors

@Composable
fun SkipperScreen(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SkipperColors.lightGrey,
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


