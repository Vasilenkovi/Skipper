package com.example.skipperproject.MobilePackage.CommonUI

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FillingAccountState(
    val surname: String = "",
    val firstName: String = "",
    val patronymic: String = "",
    val vkLink: String = "",
    val whatsappLink: String = "",
    val telegramLink: String = "",
    val isLoading: Boolean = false
)

class FillingAccountViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FillingAccountState())
    val uiState: StateFlow<FillingAccountState> = _uiState.asStateFlow()

    fun updateSurname(value: String) = _uiState.update { it.copy(surname = value) }
    fun updateFirstName(value: String) = _uiState.update { it.copy(firstName = value) }
    fun updatePatronymic(value: String) = _uiState.update { it.copy(patronymic = value) }
    fun updateVk(value: String) = _uiState.update { it.copy(vkLink = value) }
    fun updateWhatsapp(value: String) = _uiState.update { it.copy(whatsappLink = value) }
    fun updateTelegram(value: String) = _uiState.update { it.copy(telegramLink = value) }

    fun saveData() {
        // Здесь будет логика отправки на Backend
    }
}
