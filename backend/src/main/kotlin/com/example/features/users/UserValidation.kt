package com.example.features.users

import java.math.BigDecimal

fun validateRateAndDescription(hourlyRate: String?, experienceDescription: String?): String? {

    if (hourlyRate != null) {
        val rate = hourlyRate.toBigDecimalOrNull()
        if (rate == null || rate < BigDecimal.ZERO || rate > BigDecimal(10000)) {
            return "Ставка должна быть числом от 0 до 10 000 рублей"
        }
    }

    if (experienceDescription != null) {
        if (experienceDescription.length > 2000) {
            return "Описание опыта не может превышать 2000 символов"
        }

        // Используем тройные кавычки """ для регулярки, чтобы слеши работали правильно
        val linkRegex = Regex("""(?i)(https?://|www\.|[a-zA-Z0-9-]+\.[a-zA-Z]{2,}(/|\b))""")
        if (linkRegex.containsMatchIn(experienceDescription)) {
            return "В описании запрещено оставлять ссылки на сторонние ресурсы"
        }
    }

    return null
}
