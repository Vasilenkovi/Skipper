package com.example.features.users

import java.math.BigDecimal

fun validateRateAndDescription(
  hourlyRate: String?,
  experienceDescription: String?,
): String? {
  val rate = hourlyRate?.toBigDecimalOrNull()
  val linkRegex = Regex("""(?i)(https?://|www\.|[a-zA-Z0-9-]+\.[a-zA-Z]{2,}(/|\b))""")

  return when {
    hourlyRate != null && (rate == null || rate < BigDecimal.ZERO || rate > BigDecimal(10000)) ->
      "Ставка должна быть числом от 0 до 10 000 рублей"

    experienceDescription != null && experienceDescription.length > 2000 ->
      "Описание опыта не может превышать 2000 символов"

    experienceDescription != null && linkRegex.containsMatchIn(experienceDescription) ->
      "В описании запрещено оставлять ссылки на сторонние ресурсы"

    else -> null
  }
}
