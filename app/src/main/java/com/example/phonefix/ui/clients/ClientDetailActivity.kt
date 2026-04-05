package com.example.phonefix.ui.clients

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.ActivityClientDetailBinding
import com.example.phonefix.ui.repairs.RepairDetailActivity
import com.example.phonefix.ui.repairs.RepairsAdapter

class ClientDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CLIENT_ID = "extra_client_id"
    }

    private lateinit var binding: ActivityClientDetailBinding
    private lateinit var adapter: RepairsAdapter
    private var clientId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clientId = intent.getIntExtra(EXTRA_CLIENT_ID, -1)

        binding.btnBack.setOnClickListener { finish() }

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadClient()
    }

    private fun setupRecyclerView() {
        adapter = RepairsAdapter { repair ->
            val intent = Intent(this, RepairDetailActivity::class.java)
            intent.putExtra(RepairDetailActivity.EXTRA_REPAIR_ID, repair.id)
            startActivity(intent)
        }
        binding.rvRepairs.layoutManager = LinearLayoutManager(this)
        binding.rvRepairs.adapter = adapter
    }

    private fun loadClient() {
        val client = RepairRepository.getClientById(clientId)
        if (client == null) {
            finish()
            return
        }

        // Заголовок
        binding.tvClientName.text = client.name
        binding.tvClientPhone.text = client.phone
        binding.tvInitial.text = client.name.firstOrNull()?.uppercase() ?: "?"

        // Ремонти клієнта
        val repairs = RepairRepository.getRepairsByClientId(clientId)
            .sortedByDescending { it.createdDate }

        binding.tvRepairsCount.text = "Ремонтів: ${repairs.size}"
        adapter.submitList(repairs)

        binding.tvEmpty.visibility = if (repairs.isEmpty()) View.VISIBLE else View.GONE
        binding.rvRepairs.visibility = if (repairs.isEmpty()) View.GONE else View.VISIBLE
    }
}
