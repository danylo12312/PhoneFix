package com.example.phonefix.ui.repairs

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phonefix.R
import com.example.phonefix.data.model.PhoneRepair
import com.example.phonefix.data.model.RepairStatus
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.ActivityRepairDetailBinding
import com.example.phonefix.ui.clients.ClientDetailActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepairDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REPAIR_ID = "extra_repair_id"
    }

    private lateinit var binding: ActivityRepairDetailBinding
    private var repairId: Int = -1
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val priceFormat = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepairDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repairId = intent.getIntExtra(EXTRA_REPAIR_ID, -1)

        binding.btnBack.setOnClickListener { finish() }

        loadRepair()
    }

    override fun onResume() {
        super.onResume()
        loadRepair()
    }

    private fun loadRepair() {
        val repair = RepairRepository.getRepairById(repairId)
        if (repair == null) {
            finish()
            return
        }

        bindHeader(repair)
        bindStatusButtons(repair)
        bindInfo(repair)
        bindClient(repair)
        bindActions(repair)
    }

    // ─── Заголовок ───────────────────────────────────────────────

    private fun bindHeader(repair: PhoneRepair) {
        binding.tvPhoneTitle.text = "${repair.phoneBrand} ${repair.phoneModel}"
        binding.tvCreatedDate.text = "Створено: ${dateFormat.format(Date(repair.createdDate))}"

        // Бейдж статусу
        binding.tvStatusBadge.text = repair.status.displayName
        when (repair.status) {
            RepairStatus.NEW -> {
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_new)
                binding.tvStatusBadge.setTextColor(getColor(R.color.status_new))
            }
            RepairStatus.IN_PROGRESS -> {
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_in_progress)
                binding.tvStatusBadge.setTextColor(getColor(R.color.status_in_progress))
            }
            RepairStatus.DONE -> {
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_done)
                binding.tvStatusBadge.setTextColor(getColor(R.color.status_done))
            }
            RepairStatus.DELIVERED -> {
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_delivered)
                binding.tvStatusBadge.setTextColor(getColor(R.color.status_delivered))
            }
        }
    }

    // ─── 4 кнопки статусу ────────────────────────────────────────

    private fun bindStatusButtons(repair: PhoneRepair) {
        val buttons = listOf(
            binding.btnStatusNew to RepairStatus.NEW,
            binding.btnStatusInProgress to RepairStatus.IN_PROGRESS,
            binding.btnStatusDone to RepairStatus.DONE,
            binding.btnStatusDelivered to RepairStatus.DELIVERED
        )

        val currentOrdinal = repair.status.ordinal

        buttons.forEach { (btn, status) ->
            when {
                status.ordinal < currentOrdinal -> styleStatusButton(btn, State.PASSED)
                status.ordinal == currentOrdinal -> styleStatusButton(btn, State.ACTIVE)
                else -> styleStatusButton(btn, State.INACTIVE)
            }

            btn.setOnClickListener {
                RepairRepository.updateRepairStatus(repairId, status)
                loadRepair()
            }
        }
    }

    private enum class State { PASSED, ACTIVE, INACTIVE }

    private fun styleStatusButton(btn: TextView, state: State) {
        when (state) {
            State.ACTIVE -> {
                btn.setBackgroundResource(R.drawable.bg_status_btn_active)
                btn.setTextColor(getColor(android.R.color.white))
            }
            State.PASSED -> {
                btn.setBackgroundResource(R.drawable.bg_status_btn_passed)
                btn.setTextColor(getColor(R.color.status_done))
            }
            State.INACTIVE -> {
                btn.setBackgroundResource(R.drawable.bg_status_btn_inactive)
                btn.setTextColor(getColor(R.color.text_hint))
            }
        }
    }

    // ─── Інформація про ремонт ───────────────────────────────────

    private fun bindInfo(repair: PhoneRepair) {
        binding.tvImei.text = repair.imei.ifEmpty { "—" }
        binding.tvProblem.text = repair.problemDescription
        binding.tvWorkDone.text = repair.workDone.ifEmpty { "Ще не виконано" }
        binding.tvPrice.text = "${priceFormat.format(repair.price.toLong())} грн"
    }

    // ─── Блок клієнта ────────────────────────────────────────────

    private fun bindClient(repair: PhoneRepair) {
        val client = RepairRepository.getClientById(repair.clientId)
        binding.tvClientName.text = client?.name ?: "—"
        binding.tvClientPhone.text = client?.phone ?: "—"

        binding.cardClient.setOnClickListener {
            if (client != null) {
                val intent = Intent(this, ClientDetailActivity::class.java)
                intent.putExtra(ClientDetailActivity.EXTRA_CLIENT_ID, client.id)
                startActivity(intent)
            }
        }
    }

    // ─── Кнопки дій ──────────────────────────────────────────────

    private fun bindActions(repair: PhoneRepair) {
        // Редагувати
        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, NewRepairActivity::class.java)
            intent.putExtra(EXTRA_REPAIR_ID, repair.id)
            startActivity(intent)
        }

        // Наступний статус
        val nextStatus = repair.status.next()
        if (nextStatus != null) {
            binding.btnNextStatus.text = nextStatus.displayName
            binding.btnNextStatus.isEnabled = true
            binding.btnNextStatus.setOnClickListener {
                RepairRepository.updateRepairStatus(repairId, nextStatus)
                loadRepair()
            }
        } else {
            binding.btnNextStatus.text = "Завершено"
            binding.btnNextStatus.isEnabled = false
            binding.btnNextStatus.alpha = 0.5f
        }
    }
}
