package com.example.phonefix.data.repository

import com.example.phonefix.data.model.Client
import com.example.phonefix.data.model.PhoneRepair
import com.example.phonefix.data.model.RepairStatus
import java.util.Calendar

/**
 * Репозиторій з тестовими даними (Mock Data).
 * Використовується як singleton-об'єкт для збереження стану протягом сесії.
 */
object RepairRepository {

    // ---------- Клієнти ----------

    private var nextClientId = 1

    val clients: ArrayList<Client> = arrayListOf(
        Client(nextClientId++, "Олександр Петренко", "+380501234567"),
        Client(nextClientId++, "Марія Коваленко", "+380672345678"),
        Client(nextClientId++, "Іван Шевченко", "+380933456789"),
        Client(nextClientId++, "Анна Бондаренко", "+380664567890"),
        Client(nextClientId++, "Дмитро Мельник", "+380505678901")
    )

    fun addClient(name: String, phone: String): Client {
        val client = Client(nextClientId++, name, phone)
        clients.add(client)
        return client
    }

    fun getClientById(id: Int): Client? = clients.find { it.id == id }

    fun getClientByName(name: String): Client? =
        clients.find { it.name.equals(name, ignoreCase = true) }

    // ---------- Ремонти ----------

    private var nextRepairId = 1

    /** Допоміжна функція — дата N днів тому. */
    private fun daysAgo(days: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return cal.timeInMillis
    }

    val repairs: ArrayList<PhoneRepair> = arrayListOf(
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Samsung",
            phoneModel = "Galaxy S23",
            imei = "354678901234567",
            problemDescription = "Розбитий екран, не реагує на дотик",
            workDone = "Заміна дисплейного модуля",
            price = 4500.0,
            status = RepairStatus.DONE,
            clientId = 1,
            createdDate = daysAgo(5)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Apple",
            phoneModel = "iPhone 14 Pro",
            imei = "356789012345678",
            problemDescription = "Не заряджається, роз'єм Lightning пошкоджений",
            workDone = "Заміна роз'єму зарядки",
            price = 2800.0,
            status = RepairStatus.IN_PROGRESS,
            clientId = 2,
            createdDate = daysAgo(3)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Xiaomi",
            phoneModel = "Redmi Note 12",
            imei = "358901234567890",
            problemDescription = "Швидко розряджається батарея",
            workDone = "",
            price = 1200.0,
            status = RepairStatus.NEW,
            clientId = 3,
            createdDate = daysAgo(1)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Samsung",
            phoneModel = "Galaxy A54",
            imei = "351234567890123",
            problemDescription = "Не працює камера, чорний екран при відкритті",
            workDone = "Заміна модуля камери, перепрошивка",
            price = 3200.0,
            status = RepairStatus.DELIVERED,
            clientId = 1,
            createdDate = daysAgo(10)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Apple",
            phoneModel = "iPhone 13",
            imei = "359012345678901",
            problemDescription = "Телефон впав у воду, не вмикається",
            workDone = "Діагностика, чистка плати",
            price = 3500.0,
            status = RepairStatus.IN_PROGRESS,
            clientId = 4,
            createdDate = daysAgo(2)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Huawei",
            phoneModel = "P50 Pro",
            imei = "352345678901234",
            problemDescription = "Зламана кнопка живлення",
            workDone = "",
            price = 900.0,
            status = RepairStatus.NEW,
            clientId = 5,
            createdDate = daysAgo(0)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Xiaomi",
            phoneModel = "Poco X5",
            imei = "353456789012345",
            problemDescription = "Тріщина на задній кришці",
            workDone = "Заміна задньої панелі",
            price = 1500.0,
            status = RepairStatus.DONE,
            clientId = 2,
            createdDate = daysAgo(7)
        ),
        PhoneRepair(
            id = nextRepairId++,
            phoneBrand = "Samsung",
            phoneModel = "Galaxy S22 Ultra",
            imei = "354567890123456",
            problemDescription = "Проблеми зі зв'язком, пропадає мережа",
            workDone = "Ремонт антенного модуля",
            price = 2200.0,
            status = RepairStatus.DELIVERED,
            clientId = 3,
            createdDate = daysAgo(14)
        )
    )

    fun addRepair(repair: PhoneRepair): PhoneRepair {
        val newRepair = repair.copy(id = nextRepairId++)
        repairs.add(newRepair)
        return newRepair
    }

    fun getRepairById(id: Int): PhoneRepair? = repairs.find { it.id == id }

    fun getRepairsByClientId(clientId: Int): List<PhoneRepair> =
        repairs.filter { it.clientId == clientId }

    fun getRepairsCountForClient(clientId: Int): Int =
        repairs.count { it.clientId == clientId }

    fun updateRepairStatus(repairId: Int, newStatus: RepairStatus): Boolean {
        val repair = getRepairById(repairId) ?: return false
        repair.status = newStatus
        return true
    }

    fun clearAll() {
        repairs.clear()
        clients.clear()
        nextRepairId = 1
        nextClientId = 1
    }
}
