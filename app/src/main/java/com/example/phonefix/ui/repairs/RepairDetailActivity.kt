package com.example.phonefix.ui.repairs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.phonefix.data.repository.RepairRepository

/**
 * Екран деталей ремонту — заготовка.
 * Повна реалізація буде додана на Етапі 8.
 */
class RepairDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPAIR_ID = "extra_repair_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repairId = intent.getIntExtra(EXTRA_REPAIR_ID, -1)
        val repair = RepairRepository.getRepairById(repairId)

        // Заготовка — буде реалізовано на Етапі 8
        if (repair == null) {
            finish()
            return
        }

        // Тимчасово закриваємо — деталі будуть у наступній ітерації
        finish()
    }
}
