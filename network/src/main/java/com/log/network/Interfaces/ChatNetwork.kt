package com.log.network.Interfaces

import com.log.data.ChatData
import com.log.data.Message
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession

interface ChatNetwork {
    fun getNewMessages(): List<Message>
    fun getAllChats(): List<ChatData>
    fun getNewChat(): ChatData
    fun DefaultClientWebSocketSession.sendData(message: Message)
    fun setNewChat(chat: ChatData)
}