package com.log.indrop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.log.indrop.databinding.ActivityLoginBinding
import com.log.indrop.fragments.MessengerFragment
import com.log.indrop.fragments.NewsFragment
import com.log.indrop.fragments.ProfileFragment

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}