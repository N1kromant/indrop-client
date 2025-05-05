package com.log.indrop.api

import com.log.data.ChatData
import com.log.data.UserData

interface SearchApi {
    fun searchUsers(value: String): List<UserData>
    fun createNewChat(userid: Long): ChatData // Я БЛЯТЬ НЕ ЗНАЮ НАХУЯ ЭТО
}

class SearchApiImpl(): SearchApi {
    override fun searchUsers(value: String): List<UserData> {
        TODO("Реализовать searchUsers")
    }

    override fun createNewChat(userid: Long): ChatData {
        TODO("Реализовать createNewChat")
    }

}

class SearchApiTestImpl(): SearchApi {
    override fun searchUsers(value: String): List<UserData> {
        return List(1) { UserData(
            authorId = 7,
            login = "n1",
            firstName = "n1k",
            lastName = "romant",
            icon = null
        ) }
    }

    override fun createNewChat(userid: Long): ChatData {
        TODO("Реализовать createNewChat")
    }

}