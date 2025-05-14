package com.log.indrop.ViewModels.Search

import com.log.network.ViewModels.BaseMVI.BaseEffect

sealed class SearchEffect : BaseEffect {
    data class NavigateToChatEffect(
        val chatId: Long
    ) : SearchEffect()
    data object ErrorCreateChat: SearchEffect()
    data object NavigateBackEffect: SearchEffect()
}