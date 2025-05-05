package com.log.indrop.ViewModels.MessagesViewModel

import com.log.network.ViewModels.BaseMVI.BaseIntent

sealed class MessagesIntent: BaseIntent {
    data object SearchButtonPressed: MessagesIntent()
}