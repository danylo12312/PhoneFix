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
    private var editRepairId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewRepairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editRepairId = intent.getIntExtra(RepairDetailActivity.EXTRA_REPAIR_ID, -1)
        isEditMode = editRepairId != -1

        setupToolbar()
        setupClientAutocomplete()
        setupSaveButton()

        if (isEditMode) {
            loadRepairForEditing()
        }
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun loadRepairForEditing() {
        val repair = RepairRepository.getRepairById(editRepairId) ?: return
        val client = RepairRepository.getClientById(repair.clientId)

        binding.etBrand.setText(repair.phoneBrand)
        binding.etModel.setText(repair.phoneModel)
        binding.etImei.setText(repair.imei)
        binding.etProblem.setText(repair.problemDescription)
        binding.etWorkDone.setText(repair.workDone)
        binding.etPrice.setText(repair.price.toLong().toString())

        if (client != null) {
            binding.etClient.setText(client.name)
            binding.etClientPhone.setText(client.phone)
        }

        binding.btnSave.text = "Зберегти зміни"
    }

    private fun setupClientAutocomplete() {
        val clientNames = RepairRepository.clients.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, clientNames)
        binding.etClient.setAdapter(adapter)

        binding.etClient.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position) ?: return@setOnItemClickListener
            val client = RepairRepository.getClientByName(selectedName)
            if (client != null) {
                binding.etClientPhone.setText(client.phone)
                binding.etClientPhone.isEnabled = false
            }
        }

        binding.etClient.setOnDismissListener {
            val typedName = binding.etClient.text.toString().trim()
            val existing = RepairRepository.getClientByName(typedName)
            if (existing == null) {
                binding.etClientPhone.isEnabled = true
                if (!isEditMode) binding.etClientPhone.setText("")
            }
        }
    }

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

            if (isEditMode) {
                // Редагування існуючого ордеру
                val repair = RepairRepository.getRepairById(editRepairId)
                if (repair != null) {
                    repair.phoneBrand = brand
                    repair.phoneModel = model
                    repair.imei = imei
                    repair.problemDescription = problem
                    repair.workDone = workDone
                    repair.price = price
                    repair.clientId = client.id
                }
                Toast.makeText(this, "Ордер оновлено", Toast.LENGTH_SHORT).show()
            } else {
                // Створення нового ордеру
                val repair = PhoneRepair(
                    id = 0,
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
            }

            finish()
        }
    }

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
