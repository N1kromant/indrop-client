package com.log.indrop.ViewModels.MessagesViewModel

import com.log.network.ViewModels.BaseMVI.BaseEffect
import com.log.network.ViewModels.Search.SearchEffect

sealed class MessagesEffect: BaseEffect {
    data object RouteToSearch: BaseEffect
}