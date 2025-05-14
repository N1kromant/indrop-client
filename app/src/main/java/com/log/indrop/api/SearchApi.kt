package com.log.indrop.api

import com.log.data.ChatData
import com.log.data.UserData

interface SearchApi {
    fun searchUsers(value: String): List<UserData>
    fun createNewChat(userIds: List<Long>): Long? // Я БЛЯТЬ НЕ ЗНАЮ НАХУЯ ЭТО
}

class SearchApiImpl(): SearchApi {
    override fun searchUsers(value: String): List<UserData> {
        TODO("Реализовать searchUsers")
    }

    override fun createNewChat(userIds: List<Long>): Long? {
        TODO("Реализовать createNewChat")
    }

}

class SearchApiTestImpl(): SearchApi {
    override fun searchUsers(value: String): List<UserData> {
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

    override fun createNewChat(userIds: List<Long>): Long? {
        return 69L
    }
}