package com.log.indrop

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.log.indrop.databinding.ActivityMainBinding
import com.log.indrop.fragments.MessengerFragment
import com.log.indrop.fragments.NewsFragment
import com.log.indrop.fragments.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
//        supportActionBar?.hide()

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView

        // Первоначальная установка фрагмента при запуске
        val initialFragment = NewsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, initialFragment)
            .commit()

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_news -> {
                    val homeFragment = NewsFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, homeFragment)
                        .commit()
                    true
                }
                R.id.action_messenger -> {
                    val profileFragment = MessengerFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, profileFragment)
                        .commit()
                    true
                }
                R.id.action_profile -> {
                    val profileFragment = ProfileFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, profileFragment)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}