package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.R

@Composable
fun SkipperScreen2(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SkipperColors.lightGrey,
    onBackClick: (() -> Unit)? = null,
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
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Левая часть: Назад (если нужно) и Лого
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBackClick != null) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() },
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                // Логотип и текст Skipper
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.skipper_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(156.dp)
                    )
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = "Skipper",
//                        fontSize = 28.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black
//                    )
                }
            }

            // Кнопки навигации (IconButton)
            Row(
                verticalAlignment = Alignment.CenterVertically
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
