package com.log.network.Interfaces

import com.log.data.ChatData
import com.log.data.Message

interface ChatNetwork {
    fun getNewMessages(): List<Message>
    fun getAllChats(): List<ChatData>
    fun getNewChat(): ChatData
    fun send(message: Message)
    fun setNewChat(chat: ChatData)
}