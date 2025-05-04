package com.log.indrop.ViewModels.MessagesViewModel

import com.log.network.ViewModels.BaseMVI.BaseViewModel

sealed class MessagesViewModel(initialState: MessagesState) : BaseViewModel<MessagesIntent, MessagesState, MessagesEffect>(
    initialState
) {
    override suspend fun handleIntent(intent: MessagesIntent) {
        TODO("Not yet implemented")
    }

}