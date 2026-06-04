package com.example.skipperproject.MobilePackage.CommonUI

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object NetworkClient {
  private val client = OkHttpClient()
  private const val BASE_URL = "http://10.0.2.2:8080"
  private val JSON = "application/json; charset=utf-8".toMediaType()

  // Храним токен прямо в памяти (потом мобильщик перенесет его в зашифрованное хранилище)
  var authToken: String? = null

  suspend fun registerOrLogin(email: String, password: String, isLogin: Boolean): Boolean = withContext(Dispatchers.IO) {
    val jsonBody = if (isLogin) {
      """
            {
                "email": "$email",
                "passwordHash": "$password"
            }
            """.trimIndent()
    } else {
      """
            {
                "email": "$email",
                "passwordHash": "$password",
                "authProvider": "local",
                "fullName": "Новый Пользователь",
                "role": "Mentor"
            }
            """.trimIndent()
    }

    val endpoint = if (isLogin) "/api/users/login" else "/api/users/register"

    val request = Request.Builder()
      .url("$BASE_URL$endpoint")
      .post(jsonBody.toRequestBody(JSON))
      .build()

    try {
      client.newCall(request).execute().use { response ->
        val responseBody = response.body?.string()
        Log.d("SKIPPER_NETWORK", "Код ответа: ${response.code}, Тело: $responseBody")

        if (response.isSuccessful) {
          // Если это был логин, вытаскиваем токен из JSON и сохраняем!
          if (isLogin && responseBody != null) {
            try {
              val json = JSONObject(responseBody)
              authToken = json.getString("token")
              Log.d("SKIPPER_NETWORK", "Токен успешно сохранен: $authToken")
            } catch (e: Exception) {
              Log.e("SKIPPER_NETWORK", "Ошибка чтения токена", e)
            }
          }

          // ХИТРОСТЬ: Если это была регистрация, сразу делаем логин под капотом,
          // чтобы получить токен для следующего экрана (FillingAccountActivity)
          if (!isLogin) {
            return@withContext registerOrLogin(email, password, isLogin = true)
          }
          return@withContext true
        }
        return@withContext false
      }
    } catch (e: Exception) {
      Log.e("SKIPPER_NETWORK", "Ошибка соединения", e)
      return@withContext false
    }
  }

  // НОВЫЙ МЕТОД ДЛЯ ЭКРАНА ПРОФИЛЯ
  suspend fun updateProfile(fullName: String, contactInfo: String): Boolean = withContext(Dispatchers.IO) {
    if (authToken == null) {
      Log.e("SKIPPER_NETWORK", "Ошибка: нет токена для авторизации!")
      return@withContext false
    }

    val jsonBody = """
            {
                "newFullName": "$fullName",
                "contactInfo": "$contactInfo"
            }
        """.trimIndent()

    val request = Request.Builder()
      .url("$BASE_URL/api/users/profile")
      .patch(jsonBody.toRequestBody(JSON)) // Отправляем PATCH запрос, как прописано в бэкенде
      .addHeader("Authorization", "Bearer $authToken") // Передаем токен серверу
      .build()

    try {
      client.newCall(request).execute().use { response ->
        val responseBody = response.body?.string()
        Log.d("SKIPPER_NETWORK", "Обновление профиля. Код: ${response.code}, Тело: $responseBody")
        return@withContext response.isSuccessful
      }
    } catch (e: Exception) {
      Log.e("SKIPPER_NETWORK", "Ошибка соединения при обновлении профиля", e)
      return@withContext false
    }
  }

  suspend fun getProfile(): JSONObject? = withContext(Dispatchers.IO) {
    if (authToken == null) {
      Log.e("SKIPPER_NETWORK", "Ошибка: нет токена!")
      return@withContext null
    }

    val request = Request.Builder()
      .url("$BASE_URL/api/users/profile")
      .get() // Обычный GET-запрос
      .addHeader("Authorization", "Bearer $authToken")
      .build()

    try {
      client.newCall(request).execute().use { response ->
        val responseBody = response.body?.string()
        Log.d("SKIPPER_NETWORK", "Получение профиля. Код: ${response.code}, Тело: $responseBody")

        if (response.isSuccessful && responseBody != null) {
          return@withContext JSONObject(responseBody)
        }
        return@withContext null
      }
    } catch (e: Exception) {
      Log.e("SKIPPER_NETWORK", "Ошибка сети при получении профиля", e)
      return@withContext null
    }
  }

  suspend fun getMentorsList(): JSONArray? = withContext(Dispatchers.IO) {
    if (authToken == null) {
      Log.e("SKIPPER_NETWORK", "Нет токена для запроса списка менторов!")
      return@withContext null
    }

    // 1. ИСПРАВЛЕНА ССЫЛКА: теперь стучимся точно на роут /mentors
    val request = Request.Builder()
      .url("$BASE_URL/api/users/mentors")
      .get()
      .addHeader("Authorization", "Bearer $authToken")
      .build()

    try {
      client.newCall(request).execute().use { response ->
        val responseBody = response.body?.string()

        Log.d("SKIPPER_NETWORK", "Запрос списка. Код: ${response.code}")
        Log.d("SKIPPER_NETWORK", "Тело ответа: $responseBody")

        if (response.isSuccessful && responseBody != null) {
          // 2. ИСПРАВЛЕН ПАРСИНГ: Сервер отдает объект с пагинацией.
          // Сначала читаем как JSONObject, а потом достаем массив из поля "items"
          val jsonObject = JSONObject(responseBody)
          return@withContext jsonObject.getJSONArray("items")
        }
        return@withContext null
      }
    } catch (e: Exception) {
      Log.e("SKIPPER_NETWORK", "Ошибка сети при загрузке менторов", e)
      return@withContext null
    }
  }
}
