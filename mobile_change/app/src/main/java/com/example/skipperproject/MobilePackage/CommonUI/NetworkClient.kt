package com.example.skipperproject.MobilePackage.CommonUI

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object NetworkClient {
  private val client = OkHttpClient()
  // Тот самый адрес-мост из эмулятора на твой локальный компьютер
  private const val BASE_URL = "http://10.0.2.2:8080"
  private val JSON = "application/json; charset=utf-8".toMediaType()

  suspend fun registerOrLogin(email: String, password: String, isLogin: Boolean): Boolean = withContext(Dispatchers.IO) {
    // Формируем JSON вручную для максимальной простоты
    val jsonBody = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

    val endpoint = if (isLogin) "/api/users/login" else "/api/users/register"

    val request = Request.Builder()
      .url("$BASE_URL$endpoint")
      .post(jsonBody.toRequestBody(JSON))
      .build()

    try {
      client.newCall(request).execute().use { response ->
        val responseBody = response.body?.string()
        Log.d("SKIPPER_NETWORK", "Код ответа: ${response.code}, Тело: $responseBody")
        return@withContext response.isSuccessful
      }
    } catch (e: Exception) {
      Log.e("SKIPPER_NETWORK", "Ошибка соединения", e)
      return@withContext false
    }
  }
}
