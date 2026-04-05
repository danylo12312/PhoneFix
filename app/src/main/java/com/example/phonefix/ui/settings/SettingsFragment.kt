package com.example.phonefix.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.phonefix.data.repository.RepairRepository
import com.example.phonefix.databinding.FragmentSettingsBinding
import com.example.phonefix.ui.auth.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PREFS_NAME = "phonefix_prefs"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Експорт даних
        binding.btnExport.setOnClickListener {
            Toast.makeText(requireContext(), "Дані експортовано (JSON)", Toast.LENGTH_SHORT).show()
        }

        // Очистити всі дані
        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }

        // Вийти з акаунту
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * Діалог підтвердження очищення даних.
     */
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Очистити дані")
            .setMessage("Ви дійсно хочете видалити всі ремонти та клієнтів? Цю дію неможливо скасувати.")
            .setNegativeButton("Скасувати", null)
            .setPositiveButton("Видалити") { _, _ ->
                RepairRepository.clearAll()
                Toast.makeText(requireContext(), "Усі дані видалено", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    /**
     * Діалог підтвердження виходу з акаунту.
     * Після підтвердження — очищення SharedPreferences,
     * перехід на LoginActivity, очищення стеку.
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Вихід з акаунту")
            .setMessage("Ви дійсно хочете вийти з акаунту?")
            .setNegativeButton("Скасувати", null)
            .setPositiveButton("Вийти") { _, _ ->
                // Очищення SharedPreferences
                val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()

                // Перехід на LoginActivity з очищенням стеку
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
