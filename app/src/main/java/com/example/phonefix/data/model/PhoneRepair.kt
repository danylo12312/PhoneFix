package com.example.phonefix.data.model

/**
 * Модель ордеру на ремонт телефону.
 *
 * @property id                   Унікальний ідентифікатор ремонту
 * @property phoneBrand           Бренд телефону (Samsung, Apple, Xiaomi тощо)
 * @property phoneModel           Модель пристрою
 * @property imei                 IMEI-номер пристрою
 * @property problemDescription   Опис несправності
 * @property workDone             Виконані роботи
 * @property price                Вартість ремонту (грн)
 * @property status               Поточний статус ремонту
 * @property clientId             Ідентифікатор клієнта-власника
 * @property createdDate          Дата створення ордеру (мілісекунди)
 */
data class PhoneRepair(
    val id: Int,
    var phoneBrand: String,
    var phoneModel: String,
    var imei: String,
    var problemDescription: String,
    var workDone: String,
    var price: Double,
    var status: RepairStatus,
    var clientId: Int,
    val createdDate: Long
)
