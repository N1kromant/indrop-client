package com.log.indrop.navigation

import androidx.navigation.NavController
import com.log.network.ViewModels.BaseMVI.BaseEffect

interface NavigationHandler {
    fun navigate(effect: BaseEffect)
    fun setNavController(navController: NavController)
}