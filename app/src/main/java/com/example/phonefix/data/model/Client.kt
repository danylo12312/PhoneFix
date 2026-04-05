package com.example.phonefix.data.model

/**
 * Модель клієнта сервісного центру.
 *
 * @property id       Унікальний ідентифікатор клієнта
 * @property name     Ім'я клієнта
 * @property phone    Номер телефону клієнта
 */
data class Client(
    val id: Int,
    val name: String,
    val phone: String
)
