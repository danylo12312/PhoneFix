package com.example.phonefix.data.model

/**
 * Статуси ремонту телефону.
 * Новий → В роботі → Готово → Видано
 */
enum class RepairStatus(val displayName: String) {
    NEW("Новий"),
    IN_PROGRESS("В роботі"),
    DONE("Готово"),
    DELIVERED("Видано");

    /**
     * Повертає наступний статус у ланцюжку або null, якщо це останній.
     */
    fun next(): RepairStatus? = when (this) {
        NEW -> IN_PROGRESS
        IN_PROGRESS -> DONE
        DONE -> DELIVERED
        DELIVERED -> null
    }
}
