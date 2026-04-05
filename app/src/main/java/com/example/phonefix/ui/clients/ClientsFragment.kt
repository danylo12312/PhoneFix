package com.example.phonefix.ui.clients

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonefix.data.model.Client
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.FragmentClientsBinding

class ClientsFragment : Fragment() {

    private var _binding: FragmentClientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ClientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
    }

    override fun onResume() {
        super.onResume()
        loadClients()
    }

    private fun setupRecyclerView() {
        adapter = ClientsAdapter { client ->
            val intent = Intent(requireContext(), ClientDetailActivity::class.java)
            intent.putExtra(ClientDetailActivity.EXTRA_CLIENT_ID, client.id)
            startActivity(intent)
        }
        binding.rvClients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClients.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadClients(s?.toString()?.trim() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadClients(query: String = "") {
        var clients: List<Client> = RepairRepository.clients.toList()

        if (query.isNotEmpty()) {
            val q = query.lowercase()
            clients = clients.filter {
                it.name.lowercase().contains(q) || it.phone.contains(q)
            }
        }

        adapter.submitList(clients)

        binding.tvEmpty.visibility = if (clients.isEmpty()) View.VISIBLE else View.GONE
        binding.rvClients.visibility = if (clients.isEmpty()) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
