package com.example.skipperproject.CommonUI

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skipperproject.R
import com.example.skipperproject.CommonUI.SkipperScreen
import com.example.skipperproject.ui.theme.MobileTextStyles
import com.example.skipperproject.ui.theme.SkipperColors
import com.example.skipperproject.ui.theme.SkipperDimensions
import com.example.skipperproject.ui.theme.SkipperProjectTheme

class CreateAccountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                CreateAccountScreen()
            }
        }
    }
}

@Composable
fun CreateAccountScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    SkipperScreen(backgroundColor = Color(0xFFE8E8E8)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 1. Заголовок (некликабельный)
            RegistrationHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Поле Email с цифрой 1
            NumberedInputSection(
                label = stringResource(R.string.email_label_numbered),
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Поле Пароль с цифрой 2
            NumberedInputSection(
                label = stringResource(R.string.create_password_label),
                value = password,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Поле Повтор пароля с цифрой 3
            NumberedInputSection(
                label = stringResource(R.string.repeat_password_label),
                value = repeatPassword,
                onValueChange = { repeatPassword = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Кнопка подтверждения и повторная отправка
            ConfirmationRow(interactionSource = interactionSource)

            Spacer(modifier = Modifier.height(32.dp))

            // 6. Футер "Есть аккаунт? Войти"
            LoginRedirectFooter(interactionSource = interactionSource)
        }
    }
}

@Composable
fun RegistrationHeader() {
    Surface(
        color = SkipperColors.mainYellow,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = stringResource(R.string.registration_title),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MobileTextStyles.MainScreenText,
            color = Color.Black
        )
    }
}

@Composable
fun NumberedInputSection(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
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
fun ConfirmationRow(interactionSource: MutableInteractionSource) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { /* Логика подтверждения */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = SkipperColors.mainYellow,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.confirm_email_button),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = stringResource(R.string.resend_email),
            style = MobileTextStyles.SmallestText,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                /* Логика повторной отправки */
            }
        )
    }
}

@Composable
fun LoginRedirectFooter(interactionSource: MutableInteractionSource) {
    Row {
        Text(
            text = stringResource(R.string.have_account) + " ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.login_link),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                /* Переход на LoginActivity */
            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun RegistrationPreview() {
    SkipperProjectTheme {
        CreateAccountScreen()
    }
}
