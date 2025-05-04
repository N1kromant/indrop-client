package com.log.indrop.ViewModels.MessagesViewModel

import com.log.data.ChatData
import com.log.network.ViewModels.BaseMVI.BaseState

data class MessagesState (
    val isWorks: Boolean = true
//    val chats: List<ChatData?> = emptyList()
): BaseState