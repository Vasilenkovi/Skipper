package com.example.skipperproject.MobilePackage.CommonUI.FirstSteps

import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.skipperproject.MobilePackage.CommonUI.NetworkClient
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skipperproject.MobilePackage.CommonUI.FindingMentorActivity
import com.example.skipperproject.R
import com.example.skipperproject.MobilePackage.CommonUI.theme.MobileTextStyles
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperColors
import com.example.skipperproject.MobilePackage.CommonUI.theme.SkipperProjectTheme
import com.example.skipperproject.MobilePackage.CommonUI.Tools.CustomTextField
import com.example.skipperproject.MobilePackage.CommonUI.Tools.SkipperScreen

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

  // Добавляем корутины и контекст
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  SkipperScreen() {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp)
    ) {
      Spacer(modifier = Modifier.height(40.dp))
      LoginHeader(onClick = { })
      Spacer(modifier = Modifier.height(32.dp))

      EmailInput(value = email, onValueChange = { email = it })
      Spacer(modifier = Modifier.height(16.dp))

      PasswordInput(
        value = password,
        onValueChange = { password = it },
        onLoginClick = {
          // ВОТ ОНА — НАСТОЯЩАЯ ИНТЕГРАЦИЯ!
          coroutineScope.launch {
            val success = NetworkClient.registerOrLogin(email, password, isLogin = true)
            if (success) {
              // Если сервер ответил 200 ОК, пускаем дальше
              val intent = Intent(context, FindingMentorActivity::class.java)
              context.startActivity(intent)
            } else {
              // Если ошибка (неверный пароль), пока ничего не делаем,
              // но в логах Android Studio будет ошибка
              println("Ошибка авторизации!")
            }
          }
        }
      )

      Spacer(modifier = Modifier.height(12.dp))
      ForgotPassword(interactionSource = interactionSource, onClick = { })
      Spacer(modifier = Modifier.height(32.dp))
      RegistrationFooter(interactionSource = interactionSource, onRegisterClick = { })
    }
  }
}

@Composable
fun LoginHeader(onClick: () -> Unit) {
    Surface(
        color = SkipperColors.mainYellow,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = stringResource(R.string.login_title),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MobileTextStyles.ButtonsText,
            color = Color.Black
        )
    }
}

@Composable
fun EmailInput(value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(R.string.email_label),
            style = MaterialTheme.typography.bodyLarge
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
    val context = LocalContext.current
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
            onClick = {
              onLoginClick() // <--- ТЕПЕРЬ ОНА ВЫЗЫВАЕТ НАШУ ФУНКЦИЮ С СЕТЬЮ
            },
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
        modifier = Modifier
            .padding(start = 4.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
    )
}

@Composable
fun RegistrationFooter(interactionSource: MutableInteractionSource, onRegisterClick: () -> Unit) {
    val context = LocalContext.current

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
                val intent1 = Intent(context, CreateAccountActivity::class.java)
                context.startActivity(intent1)

            }
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginPreview() {
    SkipperProjectTheme {
        LoginScreen()
    }
}
