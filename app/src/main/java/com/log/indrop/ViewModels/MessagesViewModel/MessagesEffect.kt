package com.log.indrop.ViewModels.MessagesViewModel

import com.log.network.ViewModels.BaseMVI.BaseEffect

sealed class MessagesEffect: BaseEffect {
    data object RouteToSearch: MessagesEffect()
}