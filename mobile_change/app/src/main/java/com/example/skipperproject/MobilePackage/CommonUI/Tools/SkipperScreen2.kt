package com.example.skipperproject.MobilePackage.CommonUI.Tools

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.skipperproject.MobilePackage.CommonUI.FindingMentorActivity
import com.example.skipperproject.MobilePackage.CommonUI.NotificationActivity
import com.example.skipperproject.MobilePackage.CommonUI.Profiles.MentiProfileActivity
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.R

@Composable
fun SkipperScreen2(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SkipperColors.lightGrey,
    onBackClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val context = LocalContext.current

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
                
                // Логотип
                Image(
                    painter = painterResource(id = R.drawable.skipper_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(156.dp)
                )
            }

            // Кнопки навигации (IconButton)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (onMenuClick != null) {
                            onMenuClick()
                        } else {
                            val intent = Intent(context, FindingMentorActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.menu_icon),
                        contentDescription = "Menu",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = {
                        if (onNotificationsClick != null) {
                            onNotificationsClick()
                        } else {
                            val intent = Intent(context, NotificationActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notification_icon),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
                IconButton(
                    onClick = {
                        if (onProfileClick != null) {
                            onProfileClick()
                        } else {
                            val intent = Intent(context, MentiProfileActivity::class.java)
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.profile_icon),
                        contentDescription = "Profile",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
            }
        }
    }
}
