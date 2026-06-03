package com.example.skipperproject.MobilePackage.CommonUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skipperproject.R
import com.example.skipperproject.MobilePackage.CommonUI.Tools.CustomTextField
import com.example.skipperproject.MobilePackage.CommonUI.Tools.SkipperScreen
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme

class FillingAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                FillingAccountScreen()
            }
        }
    }
}

@Composable
fun FillingAccountScreen(viewModel: FillingAccountViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    SkipperScreen(backgroundColor = Color(0xFFE8E8E8)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Давайте\nзнакомиться",
                style = MobileTextStyles.MainScreenText.copy(
                    fontSize = 36.sp,
                    lineHeight = 32.sp
                ),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            FillingInputSection(
                label = "Фамилия",
                value = uiState.surname,
                onValueChange = { viewModel.updateSurname(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FillingInputSection(
                label = "Имя",
                value = uiState.firstName,
                onValueChange = { viewModel.updateFirstName(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FillingInputSection(
                label = "Отчество (при наличии)",
                value = uiState.patronymic,
                onValueChange = { viewModel.updatePatronymic(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Ваши соцсети (минимум одна)",
                style = MobileTextStyles.QuestionText.copy(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            SocialInputRow(
                iconRes = R.drawable.vk_icon,
                value = uiState.vkLink,
                onValueChange = { viewModel.updateVk(it) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialInputRow(
                iconRes = R.drawable.whatsapp_icon,
                value = uiState.whatsappLink,
                onValueChange = { viewModel.updateWhatsapp(it) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SocialInputRow(
                iconRes = R.drawable.telegram_icon,
                value = uiState.telegramLink,
                onValueChange = { viewModel.updateTelegram(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveData() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkipperColors.mainYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "Вперёд к поиску!",
                    style = MobileTextStyles.QuestionText,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FillingInputSection(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MobileTextStyles.QuestionText.copy(fontSize = 14.sp),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SocialInputRow(iconRes: Int, value: String, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        CustomTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun FillingAccountPreview() {
    SkipperProjectTheme {
        FillingAccountScreen()
    }
}
