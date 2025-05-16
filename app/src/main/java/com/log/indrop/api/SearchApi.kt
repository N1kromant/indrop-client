package com.log.indrop.api

import com.apollographql.apollo3.ApolloClient
import com.log.data.UserData
import com.log.data.ChatData
import com.log.data.Message
import com.log.data.Content

import com.example.graphql.*
import com.log.network.NetworkManager
import java.time.OffsetDateTime


interface SearchApi {
    suspend fun searchUsers(value: String): List<UserData>
    suspend fun createChat(title: String, avatar: String?, memberIds: List<Int>): NetworkManager.ChatCreationResult
    suspend fun getChats(userId: Int): List<ChatData>
}

class SearchApiImpl(_networkManager: NetworkManager): SearchApi {

    private val networkManager = _networkManager
    override suspend fun searchUsers(value: String): List<UserData> {

        val client = networkManager.apolloClient

        return try {
            val response = client.query(SearchUsersQuery(value)).execute()

            val data = response.data
            if (data == null || response.hasErrors()) {
                emptyList()
            } else {
                data.searchUsers.map {
                    UserData(
                        authorId = it.authorId.toLong(),
                        login = it.login,
                        firstName = it.firstName,
                        lastName = it.lastName,
                        icon = it.icon
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun createChat(title: String, avatar: String?, memberIds: List<Int>): NetworkManager.ChatCreationResult {

        val client = networkManager.apolloClient

        try {
            // Создаем мутацию с правильными параметрами
            val mutation = AddChatMutation(
                title = title,
                avatar = avatar,
                memberIds = memberIds
            )

            val response = client.mutation(mutation).execute()

            if (response.hasErrors()) {
                return NetworkManager.ChatCreationResult(false)
            }

            // Проверяем данные ответа на null
            val data = response.data
            if (data == null) {
                return NetworkManager.ChatCreationResult(false)
            }

            val success = data.addChat.success
            val chatId = data.addChat.chatId?.toIntOrNull()

            return NetworkManager.ChatCreationResult(success, chatId)
        } catch (e: Exception) {
            e.printStackTrace() // Полный стек вызовов для лучшей диагностики
            return NetworkManager.ChatCreationResult(false, null)
        }
    }

    override suspend fun getChats(userId: Int): List<ChatData> {
        val client = networkManager.apolloClient
        return try {
            val response = client.query(GetChatsQuery(userId = userId.toString())).execute()
            if (response.hasErrors()) {
                emptyList()
            } else {
                mapChats(response.data)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }


    fun mapChats(response: GetChatsQuery.Data?): List<ChatData> {
        if (response == null || response.chats == null) return emptyList()

        return response.chats.mapNotNull { chat ->
            val messages = chat.messages?.mapNotNull { message ->
                try {
                    Message(
                        messageId = message.messageId.toLongOrNull(),
                        author = UserData(
                            authorId = message.author.authorId.toLongOrNull(),
                            login = message.author.login,
                            firstName = message.author.firstName,
                            lastName = message.author.lastName,
                            icon = message.author.icon
                        ),
                        dateTime = OffsetDateTime.parse(message.dateTime),
                        content = Content(
                            text = message.content.text,
                            images = message.content.images
                        ),
                        isReplyTo = message.isReplyTo?.toLongOrNull()
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()

            try {
                ChatData(
                    chatId = chat.chatId.toLong(),
                    title = chat.title,
                    avatar = chat.avatar,
                    members = chat.members.map { it }.toMutableList() ?: mutableListOf(),
                    messages = messages
                )
            } catch (e: Exception) {
                null
            }
        }
    }


}

class SearchApiTestImpl(): SearchApi {
    override suspend fun searchUsers(value: String): List<UserData> {
        return listOf(
            UserData(
                authorId = 1L,
                login = "user1",
                firstName = "Иван",
                lastName = "Зубарев",
                icon = null
            ),
            UserData(
                authorId = 2L,
                login = "user2",
                firstName = "Александр",
                lastName = "Золо",
                icon = null
            )
        )
    }

    override suspend fun createChat(title: String, avatar: String?, memberIds: List<Int>): NetworkManager.ChatCreationResult {
        return NetworkManager.ChatCreationResult(success = true, chatId = 69)
    }

    override suspend fun getChats(userId: Int): List<ChatData> {
        return listOf()
    }

}