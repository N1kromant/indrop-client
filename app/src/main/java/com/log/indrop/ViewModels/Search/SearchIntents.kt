package com.log.indrop.ViewModels.Search

import com.log.network.ViewModels.BaseMVI.BaseIntent

sealed class SearchIntent : BaseIntent {
    data class SearchFieldChangedIntent(
        val newValue: String
    ): SearchIntent()

    data class ChatPressedIntent(
        val chatId: Long
    ): SearchIntent()

    data object GoBackIntent: SearchIntent()
}
