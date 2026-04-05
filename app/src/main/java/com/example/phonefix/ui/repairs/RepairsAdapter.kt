package com.example.phonefix.ui.repairs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonefix.R
import com.example.phonefix.data.model.PhoneRepair
import com.example.phonefix.data.model.RepairStatus
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.ItemRepairBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RepairsAdapter(
    private val onItemClick: (PhoneRepair) -> Unit
) : ListAdapter<PhoneRepair, RepairsAdapter.RepairViewHolder>(RepairDiffCallback()) {

    private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    private val priceFormat = java.text.DecimalFormat("#,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepairViewHolder {
        val binding = ItemRepairBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RepairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepairViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RepairViewHolder(
        private val binding: ItemRepairBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(repair: PhoneRepair) {
            val context = binding.root.context

            // Бренд + Модель
            binding.tvPhoneBrand.text = "${repair.phoneBrand} ${repair.phoneModel}"

            // Опис несправності
            binding.tvProblem.text = repair.problemDescription

            // Ім'я клієнта
            val client = RepairRepository.getClientById(repair.clientId)
            binding.tvClientName.text = client?.name ?: "—"

            // Дата
            binding.tvDate.text = dateFormat.format(Date(repair.createdDate))

            // Ціна
            binding.tvPrice.text = "${priceFormat.format(repair.price.toLong())} грн"

            // Статус — бейдж
            binding.tvStatus.text = repair.status.displayName
            when (repair.status) {
                RepairStatus.NEW -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_new)
                    binding.tvStatus.setTextColor(context.getColor(R.color.status_new))
                }
                RepairStatus.IN_PROGRESS -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_in_progress)
                    binding.tvStatus.setTextColor(context.getColor(R.color.status_in_progress))
                }
                RepairStatus.DONE -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_done)
                    binding.tvStatus.setTextColor(context.getColor(R.color.status_done))
                }
                RepairStatus.DELIVERED -> {
                    binding.tvStatus.setBackgroundResource(R.drawable.bg_status_delivered)
                    binding.tvStatus.setTextColor(context.getColor(R.color.status_delivered))
                }
            }

            // Клік на картку
            binding.root.setOnClickListener { onItemClick(repair) }
        }
    }

    class RepairDiffCallback : DiffUtil.ItemCallback<PhoneRepair>() {
        override fun areItemsTheSame(oldItem: PhoneRepair, newItem: PhoneRepair): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PhoneRepair, newItem: PhoneRepair): Boolean =
            oldItem == newItem
    }
}
