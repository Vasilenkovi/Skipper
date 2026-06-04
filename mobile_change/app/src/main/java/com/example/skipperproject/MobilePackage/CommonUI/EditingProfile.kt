package com.example.skipperproject.MobilePackage.CommonUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.skipperproject.R
import com.example.skipperproject.MobilePackage.CommonUI.Tools.CustomTextField
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors

@Composable
fun EditingProfile(
    onDismiss: () -> Unit,
    onSave: () -> Unit // Здесь можно добавить передачу данных
) {
    var surname by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var patronymic by remember { mutableStateOf("") }
    var vkLink by remember { mutableStateOf("") }
    var whatsappLink by remember { mutableStateOf("") }
    var telegramLink by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clickable(enabled = false) { }, // Предотвращаем закрытие при клике на саму карточку
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8E8E8)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Редактирование профиля",
                        style = MobileTextStyles.QuestionText.copy(fontSize = 20.sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Плейсхолдер для фото профиля
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .background(Color(0xFF666666), RoundedCornerShape(12.dp))
                            .clickable { /* Логика выбора фото */ }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    EditFieldSection(label = "Фамилия", value = surname, onValueChange = { surname = it })
                    Spacer(modifier = Modifier.height(14.dp))
                    EditFieldSection(label = "Имя", value = name, onValueChange = { name = it })
                    Spacer(modifier = Modifier.height(14.dp))
                    EditFieldSection(label = "Отчество (при наличии)", value = patronymic, onValueChange = { patronymic = it })

                    Spacer(modifier = Modifier.height(28.dp))

                    SocialEditRow(iconRes = R.drawable.vk_icon, value = vkLink, onValueChange = { vkLink = it })
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialEditRow(iconRes = R.drawable.whatsapp_icon, value = whatsappLink, onValueChange = { whatsappLink = it })
                    Spacer(modifier = Modifier.height(12.dp))
                    SocialEditRow(iconRes = R.drawable.telegram_icon, value = telegramLink, onValueChange = { telegramLink = it })

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkipperColors.mainYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(27.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Сохранить изменения",
                            style = MobileTextStyles.QuestionText.copy(fontSize = 16.sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditFieldSection(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MobileTextStyles.QuestionText.copy(fontSize = 16.sp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SocialEditRow(iconRes: Int, value: String, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun EditPreview(){
    EditingProfile(onDismiss = {}, onSave = {})
}
