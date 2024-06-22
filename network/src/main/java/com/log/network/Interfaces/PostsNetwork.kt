package com.log.network.Interfaces

import com.log.data.Comment
import com.log.data.Message
import com.log.data.PostData

interface PostsNetwork {
//    /**
//     * Получение списка всех комментариев поста
//     */
//    fun getAllComments(postId: Long): List<Message>

    /**
     * Отправка нового поста на сервер
     */
    fun setPost(post: PostData)
    /**
     * Отправка лайка на сервер
     * (сервер меняет значение в бд на противоположное)
     */
    fun setLike(post: PostData)

    /**
     * Отправка комментария на сервер
     */
    fun sendData(message: Comment)
}