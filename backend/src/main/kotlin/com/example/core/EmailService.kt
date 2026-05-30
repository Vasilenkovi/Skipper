package com.example.core

import com.example.features.users.Users.email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

object EmailService {
  private const val SMTP_HOST = "smtp.mail.ru"
  private const val SMTP_PORT = 465
  private val BOT_EMAIL = System.getenv("SMTP_USER") ?: ""
  private val APP_PASSWORD = System.getenv("SMTP_PASSWORD") ?: ""

  suspend fun sendConfirmationCode(
    userEmail: String,
    code: String,
  ) {
    if (System.getenv("SMTP_PASSWORD").isNullOrBlank()) {
      println("Test mode: Skipping real email send to $email")
      return
    }

    withContext(Dispatchers.IO) {
      try {
        val email = SimpleEmail()
        email.hostName = SMTP_HOST
        email.setSmtpPort(SMTP_PORT)
        email.setAuthenticator(DefaultAuthenticator(BOT_EMAIL, APP_PASSWORD))
        email.isSSLOnConnect = true

        email.setFrom(BOT_EMAIL, "Skipper App")
        email.subject = "Код подтверждения регистрации"
        email.setMsg(
          """
          Добро пожаловать в Skipper!
          
          Твой код для подтверждения почты: $code
          
          Никому не сообщай этот код.
          """.trimIndent(),
        )
        email.addTo(userEmail)

        email.send()
        println("✅ Письмо успешно отправлено на $userEmail")
      } catch (e: javax.mail.MessagingException) {
        println("Ошибка почтового сервера: ${e.message}")
      } catch (e: java.net.ConnectException) {
        println("Ошибка сети: ${e.message}")
      }
    }
  }
}
