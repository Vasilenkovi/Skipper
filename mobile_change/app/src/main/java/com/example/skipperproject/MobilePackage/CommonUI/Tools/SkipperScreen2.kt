package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.R

data class Mentor(
    val name: String,
    val description: String,
    val photo: Painter? = null
)

@Composable
fun SkipperScreen2(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SkipperColors.lightGrey,
    onMenuClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Верхняя желтая полоса
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(SkipperColors.mainYellow)
        )

        // Основной контент
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }

        // Нижняя желтая панель с логотипом и кнопками
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(SkipperColors.mainYellow)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Только логотип из ресурсов
            Image(
                painter = painterResource(id = R.drawable.skipper_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(156.dp)
            )

            // Кнопки навигации (IconButton)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Outlined.GridView,
                        contentDescription = "Menu",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
