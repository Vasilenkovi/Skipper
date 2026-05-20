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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skipperproject.R
import com.example.skipperproject.ui.theme.MobileTextStyles
import com.example.skipperproject.ui.theme.SkipperColors
import com.example.skipperproject.ui.theme.SkipperDimensions
import com.example.skipperproject.ui.theme.SkipperProjectTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkipperProjectTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    SkipperScreen() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            LoginHeader(onClick = { /* Действие при нажатии на заголовок */ })

            Spacer(modifier = Modifier.height(32.dp))

            EmailInput(
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                value = password,
                onValueChange = { password = it },
                onLoginClick = { /* Логика входа */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ForgotPassword(
                interactionSource = interactionSource,
                onClick = { /* Логика восстановления пароля */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            RegistrationFooter(
                interactionSource = interactionSource,
                onRegisterClick = { /* Логика перехода к регистрации */ }
            )
        }
    }
}

@Composable
fun LoginHeader(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SkipperColors.mainYellow,
            contentColor = Color.Black // Цвет текста на жёлтом фоне
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.login_title),
            style = MobileTextStyles.ButtonsText, //
        )
    }
}

@Composable
fun EmailInput(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.email_label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
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
fun PasswordInput(value: String, onValueChange: (String) -> Unit, onLoginClick: () -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.password_label),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = onLoginClick,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.loginicon),
                    contentDescription = "Login",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@Composable
fun ForgotPassword(interactionSource: MutableInteractionSource, onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.forgot_password),
        style = MaterialTheme.typography.bodySmall,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable{}
            .padding(start = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()//сюда вставить действие
            }
    )
}

@Composable
fun RegistrationFooter(interactionSource: MutableInteractionSource, onRegisterClick: () -> Unit) {
    Row {
        Text(
            text = stringResource(R.string.no_account) + " ",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.register),
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = TextDecoration.Underline,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onRegisterClick()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        shape = RoundedCornerShape(SkipperDimensions.inputFieldRound),
        singleLine = true,
        textStyle = MobileTextStyles.HintText
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoginPreview() {
    SkipperProjectTheme {
        LoginScreen()
    }
}
