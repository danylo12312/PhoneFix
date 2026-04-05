package com.example.phonefix.ui.clients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonefix.data.model.Client
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.ItemClientBinding

class ClientsAdapter(
    private val onItemClick: (Client) -> Unit
) : ListAdapter<Client, ClientsAdapter.ClientViewHolder>(ClientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClientViewHolder(
        private val binding: ItemClientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(client: Client) {
            binding.tvClientName.text = client.name
            binding.tvClientPhone.text = client.phone

            // Перша літера імені як аватар
            binding.tvInitial.text = client.name.firstOrNull()?.uppercase() ?: "?"

            // Кількість ремонтів
            val count = RepairRepository.getRepairsCountForClient(client.id)
            binding.tvRepairsCount.text = count.toString()

            binding.root.setOnClickListener { onItemClick(client) }
        }
    }

    class ClientDiffCallback : DiffUtil.ItemCallback<Client>() {
        override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean =
            oldItem == newItem
    }
}
