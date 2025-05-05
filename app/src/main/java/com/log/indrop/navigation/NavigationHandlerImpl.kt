package com.log.indrop.navigation

import androidx.navigation.NavController
import com.log.indrop.ViewModels.MessagesViewModel.MessagesEffect
import com.log.network.ViewModels.BaseMVI.BaseEffect
import java.util.concurrent.atomic.AtomicReference

class NavigationHandlerImpl : NavigationHandler {
    private val navControllerRef = AtomicReference<NavController?>(null)

    override fun setNavController(navController: NavController) {
        navControllerRef.set(navController)
    }

    override fun navigate(effect: BaseEffect) {
        val navController = navControllerRef.get() ?: return

        when (effect) {
            is MessagesEffect.RouteToSearch -> navController.navigate("search") { popUpTo("messages") }
            else -> { /* Игнорировать другие эффекты */ }
        }
    }
}