package com.log.indrop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.log.indrop.databinding.FragmentNewsBinding

class NewsFragment: Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

//        val navController =
//            binding.fragmentContainerView.navController
//
//
//
//        val navController = Navigation.findNavController(binding.fragmentContainerView)
////        val navController = findNavController(R.id.fragmentContainerView)
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.newsFragment,
//                R.id.messengerFragment,
//                R.id.profileFragment
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        val bottomNavigationView = binding.bottomNavigationView
//        bottomNavigationView.setupWithNavController(navController)

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}