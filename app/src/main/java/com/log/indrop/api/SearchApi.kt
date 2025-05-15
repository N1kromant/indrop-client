package com.log.indrop.api

import com.apollographql.apollo3.ApolloClient
import com.log.data.UserData

import com.example.graphql.*
import com.log.network.NetworkManager


interface SearchApi {
    suspend fun searchUsers(value: String): List<UserData>
    suspend fun createChat(title: String, avatar: String?, memberIds: List<Int>): NetworkManager.ChatCreationResult
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
}