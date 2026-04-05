package com.example.phonefix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.phonefix.databinding.ActivityMainBinding
import com.example.phonefix.ui.clients.ClientsFragment
import com.example.phonefix.ui.repairs.RepairsFragment
import com.example.phonefix.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val repairsFragment = RepairsFragment()
    private val clientsFragment = ClientsFragment()
    private val settingsFragment = SettingsFragment()
    private var activeFragment: Fragment = repairsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragments()
        setupBottomNavigation()
    }

    private fun setupFragments() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragmentContainer, settingsFragment, "settings").hide(settingsFragment)
            add(R.id.fragmentContainer, clientsFragment, "clients").hide(clientsFragment)
            add(R.id.fragmentContainer, repairsFragment, "repairs")
        }.commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_repairs -> repairsFragment
                R.id.nav_clients -> clientsFragment
                R.id.nav_settings -> settingsFragment
                else -> repairsFragment
            }
            switchFragment(fragment)
            true
        }
    }

    private fun switchFragment(target: Fragment) {
        if (target == activeFragment) return
        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment)
            show(target)
        }.commit()
        activeFragment = target
    }
}
