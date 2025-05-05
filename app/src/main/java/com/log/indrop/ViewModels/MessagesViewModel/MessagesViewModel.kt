package com.log.indrop.ViewModels.MessagesViewModel

import com.log.network.ViewModels.BaseMVI.BaseViewModel

class MessagesViewModel() : BaseViewModel<MessagesIntent, MessagesState, MessagesEffect>(
    MessagesState()
) {
    override suspend fun handleIntent(intent: MessagesIntent) {
        when(intent) {
            MessagesIntent.SearchButtonPressed -> {
                 emitEffect(effect = MessagesEffect.RouteToSearch)
            }
        }
    }

}