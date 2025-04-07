package com.log.network.ViewModels.Search

import com.log.network.ViewModels.BaseMVI.BaseEffect

sealed class SearchEffect : BaseEffect {
    data class NavigateToChatEffect(
        val chatId: Long
    ) : SearchEffect()

    data object NavigateBackEffect: SearchEffect()
}