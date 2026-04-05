package com.example.phonefix.ui.repairs

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonefix.R
import com.example.phonefix.data.model.PhoneRepair
import com.example.phonefix.data.model.RepairStatus
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.FragmentRepairsBinding

class RepairsFragment : Fragment() {

    private var _binding: FragmentRepairsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RepairsAdapter
    private var currentFilter: RepairStatus? = null  // null = Всі
    private var currentQuery: String = ""
    private var currentSort: SortOption = SortOption.DATE_NEW

    enum class SortOption {
        DATE_NEW, DATE_OLD, PRICE_HIGH, PRICE_LOW, BRAND_AZ
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRepairsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupFilterChips()
        setupSort()
        setupFab()
    }

    override fun onResume() {
        super.onResume()
        applyFilters()
    }

    // ─── RecyclerView ────────────────────────────────────────────

    private fun setupRecyclerView() {
        adapter = RepairsAdapter { repair ->
            // Перехід на екран деталей ремонту (заготовка)
            val intent = Intent(requireContext(), RepairDetailActivity::class.java)
            intent.putExtra(RepairDetailActivity.EXTRA_REPAIR_ID, repair.id)
            startActivity(intent)
        }
        binding.rvRepairs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRepairs.adapter = adapter
    }

    // ─── Пошук ───────────────────────────────────────────────────

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentQuery = s?.toString()?.trim() ?: ""
                applyFilters()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ─── Фільтрація за статусом (Chip-кнопки) ───────────────────

    private fun setupFilterChips() {
        val chips = listOf(
            binding.chipAll to null,
            binding.chipNew to RepairStatus.NEW,
            binding.chipInProgress to RepairStatus.IN_PROGRESS,
            binding.chipDone to RepairStatus.DONE,
            binding.chipDelivered to RepairStatus.DELIVERED
        )

        chips.forEach { (chipView, status) ->
            chipView.setOnClickListener {
                currentFilter = status
                updateChipStyles(chipView, chips.map { it.first })
                applyFilters()
            }
        }
    }

    private fun updateChipStyles(selected: TextView, allChips: List<TextView>) {
        allChips.forEach { chip ->
            if (chip == selected) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(resources.getColor(android.R.color.white, null))
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(resources.getColor(R.color.text_secondary, null))
            }
        }
    }

    // ─── Сортування ──────────────────────────────────────────────

    private fun setupSort() {
        binding.btnSort.setOnClickListener { view ->
            val popup = PopupMenu(requireContext(), view)
            popup.menu.apply {
                add(0, 0, 0, getString(R.string.sort_date_new))
                add(0, 1, 1, getString(R.string.sort_date_old))
                add(0, 2, 2, getString(R.string.sort_price_high))
                add(0, 3, 3, getString(R.string.sort_price_low))
                add(0, 4, 4, getString(R.string.sort_brand_az))
            }
            popup.setOnMenuItemClickListener { item ->
                currentSort = when (item.itemId) {
                    0 -> SortOption.DATE_NEW
                    1 -> SortOption.DATE_OLD
                    2 -> SortOption.PRICE_HIGH
                    3 -> SortOption.PRICE_LOW
                    4 -> SortOption.BRAND_AZ
                    else -> SortOption.DATE_NEW
                }
                applyFilters()
                true
            }
            popup.show()
        }
    }

    // ─── FAB ─────────────────────────────────────────────────────

    private fun setupFab() {
        binding.fabNewRepair.setOnClickListener {
            val intent = Intent(requireContext(), NewRepairActivity::class.java)
            startActivity(intent)
        }
    }

    // ─── Фільтрація + Сортування + Пошук ────────────────────────

    private fun applyFilters() {
        var result: List<PhoneRepair> = RepairRepository.repairs.toList()

        // Фільтр за статусом
        if (currentFilter != null) {
            result = result.filter { it.status == currentFilter }
        }

        // Пошук
        if (currentQuery.isNotEmpty()) {
            val q = currentQuery.lowercase()
            result = result.filter { repair ->
                repair.phoneBrand.lowercase().contains(q) ||
                repair.phoneModel.lowercase().contains(q) ||
                (RepairRepository.getClientById(repair.clientId)?.name?.lowercase()?.contains(q) == true)
            }
        }

        // Сортування
        result = when (currentSort) {
            SortOption.DATE_NEW -> result.sortedByDescending { it.createdDate }
            SortOption.DATE_OLD -> result.sortedBy { it.createdDate }
            SortOption.PRICE_HIGH -> result.sortedByDescending { it.price }
            SortOption.PRICE_LOW -> result.sortedBy { it.price }
            SortOption.BRAND_AZ -> result.sortedBy { it.phoneBrand.lowercase() }
        }

        adapter.submitList(result)

        // Показати/приховати пустий стан
        binding.tvEmpty.visibility = if (result.isEmpty()) View.VISIBLE else View.GONE
        binding.rvRepairs.visibility = if (result.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
