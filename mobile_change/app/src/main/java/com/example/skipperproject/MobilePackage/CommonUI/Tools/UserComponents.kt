package com.example.skipperproject.MobilePackage.CommonUI.Tools

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.R

@Composable
fun UserSocialIconSmall(resId: Int, onClick: () -> Unit = {}) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .size(32.dp)
            .clickable { onClick() }
    )
}

@Composable
fun UserProfileHeader(
    name: String,
    role: String,
    photo: Painter? = null,
    onEditClick: () -> Unit = {},
    onVkClick: () -> Unit = {},
    onWhatsappClick: () -> Unit = {},
    onTelegramClick: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Верхняя строка: Заголовок, Иконка правки и Бейдж роли
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Личный профиль",
                        style = MobileTextStyles.QuestionText.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onEditClick() },
                        tint = Color.Gray
                    )
                }

                // Бейдж роли (ментор/менти)
                Surface(
                    color = SkipperColors.mainYellow,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = role,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MobileTextStyles.SmallestText.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Основной контент: Фото и Инфо
            Row(verticalAlignment = Alignment.Top) {
                // Фото пользователя
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(Color(0xFFBEBEBE), RoundedCornerShape(12.dp))
                ) {
                    photo?.let {
                        Image(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Имя и соцсети
                Column {
                    Text(
                        text = name,
                        style = MobileTextStyles.QuestionText.copy(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UserSocialIconSmall(R.drawable.vk_icon, onClick = onVkClick)
                        UserSocialIconSmall(R.drawable.whatsapp_icon, onClick = onWhatsappClick)
                        UserSocialIconSmall(R.drawable.telegram_icon, onClick = onTelegramClick)
                    }
                }
            }
        }
    }
}