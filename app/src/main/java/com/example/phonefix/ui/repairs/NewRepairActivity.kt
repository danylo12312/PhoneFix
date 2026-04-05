package com.example.phonefix.ui.repairs

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phonefix.data.model.PhoneRepair
import com.example.phonefix.data.model.RepairStatus
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.ActivityNewRepairBinding

class NewRepairActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRepairBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClientAutocomplete()
        setupSaveButton()
    }

    // ─── Toolbar ─────────────────────────────────────────────────

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    // ─── Автозаповнення клієнта ──────────────────────────────────

    private fun setupClientAutocomplete() {
        val clientNames = RepairRepository.clients.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientNames)
        binding.etClient.setAdapter(adapter)

        // Коли обраний існуючий клієнт — підставити телефон
        binding.etClient.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position) ?: return@setOnItemClickListener
            val client = RepairRepository.getClientByName(selectedName)
            if (client != null) {
                binding.etClientPhone.setText(client.phone)
                binding.etClientPhone.isEnabled = false
            }
        }

        // Якщо текст змінюється вручну — дозволити редагувати телефон
        binding.etClient.setOnDismissListener {
            val typedName = binding.etClient.text.toString().trim()
            val existing = RepairRepository.getClientByName(typedName)
            if (existing == null) {
                binding.etClientPhone.isEnabled = true
                binding.etClientPhone.setText("")
            }
        }
    }

    // ─── Збереження ордеру ────────────────────────────────────────

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (!validateFields()) return@setOnClickListener

            val brand = binding.etBrand.text.toString().trim()
            val model = binding.etModel.text.toString().trim()
            val imei = binding.etImei.text.toString().trim()
            val problem = binding.etProblem.text.toString().trim()
            val workDone = binding.etWorkDone.text.toString().trim()
            val price = binding.etPrice.text.toString().trim().toDoubleOrNull() ?: 0.0
            val clientName = binding.etClient.text.toString().trim()
            val clientPhone = binding.etClientPhone.text.toString().trim()

            // Знайти або створити клієнта
            var client = RepairRepository.getClientByName(clientName)
            if (client == null) {
                if (clientPhone.isEmpty()) {
                    binding.etClientPhone.error = "Вкажіть телефон нового клієнта"
                    binding.etClientPhone.requestFocus()
                    return@setOnClickListener
                }
                client = RepairRepository.addClient(clientName, clientPhone)
            }

            // Створити ордер
            val repair = PhoneRepair(
                id = 0, // буде призначено в репозиторії
                phoneBrand = brand,
                phoneModel = model,
                imei = imei,
                problemDescription = problem,
                workDone = workDone,
                price = price,
                status = RepairStatus.NEW,
                clientId = client.id,
                createdDate = System.currentTimeMillis()
            )

            RepairRepository.addRepair(repair)

            Toast.makeText(this, "Ордер створено", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // ─── Валідація ────────────────────────────────────────────────

    private fun validateFields(): Boolean {
        var isValid = true

        if (binding.etBrand.text.isNullOrBlank()) {
            binding.etBrand.error = "Обов'язкове поле"
            isValid = false
        }
        if (binding.etModel.text.isNullOrBlank()) {
            binding.etModel.error = "Обов'язкове поле"
            isValid = false
        }
        if (binding.etProblem.text.isNullOrBlank()) {
            binding.etProblem.error = "Обов'язкове поле"
            isValid = false
        }
        if (binding.etClient.text.isNullOrBlank()) {
            binding.etClient.error = "Обов'язкове поле"
            isValid = false
        }
        if (binding.etPrice.text.isNullOrBlank()) {
            binding.etPrice.error = "Обов'язкове поле"
            isValid = false
        }

        return isValid
    }
}
