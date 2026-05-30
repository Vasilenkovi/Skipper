package com.example.core

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object EmailService {
    private val smtpHost = "smtp.mail.ru"
    private val smtpPort = 465
    private val botEmail = System.getenv("SMTP_USER") ?: ""
    private val appPassword = System.getenv("SMTP_PASSWORD") ?: ""

    suspend fun sendConfirmationCode(userEmail: String, code: String) {
        withContext(Dispatchers.IO) {
            try {
                val email = SimpleEmail()
                email.hostName = smtpHost
                email.setSmtpPort(smtpPort)
                email.setAuthenticator(DefaultAuthenticator(botEmail, appPassword))
                email.isSSLOnConnect = true

                email.setFrom(botEmail, "Skipper App")
                email.subject = "Код подтверждения регистрации"
                email.setMsg(
                    """
                    Добро пожаловать в Skipper!
                    
                    Твой код для подтверждения почты: $code
                    
                    Никому не сообщай этот код.
                    """.trimIndent()
                )
                email.addTo(userEmail)

                email.send()
                println("✅ Письмо успешно отправлено на $userEmail")
            } catch (e: Exception) {
                println("❌ Ошибка при отправке письма: ${e.message}")
            }
        }
    }
}
