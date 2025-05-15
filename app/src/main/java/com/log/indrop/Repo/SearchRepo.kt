package com.log.indrop.Repo

import com.log.data.UserData
import com.log.indrop.api.SearchApi

/**
 * Интерфейс репозитория поиска
 */
interface SearchRepository {
    /**
     * Выполняет поиск по заданному запросу
     * @param query строка запроса
     * @return Flow с результатами поиска
     */
    suspend fun search(query: String, users: (List<UserData>) -> Unit)
    suspend fun createChat(title: String, avatar: String?, userIds: List<Long>, chatId: (Long?) -> Unit)

}

/**
 * Реализация репозитория поиска
 */
class SearchRepositoryImpl(
    private val searchApi: SearchApi,
//    private val searchDao: SearchDao
) : SearchRepository {

    override suspend fun search(query: String, users: (List<UserData>) -> Unit) {
        try {
            // Получаем результаты поиска из API
            val results = searchApi.searchUsers(query)

            return users(results)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createChat(title: String, avatar: String?, userIds: List<Long>, chatId: (Long?) -> Unit) {
        try {

            val response = searchApi.createChat(title, avatar, userIds.map {it.toInt()})
            return chatId(response.chatId?.toLong())
                         } catch (e: Exception) {
            throw e
        }
    }



}