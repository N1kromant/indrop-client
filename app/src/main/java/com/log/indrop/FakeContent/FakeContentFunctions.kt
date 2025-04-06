package com.log.indrop.FakeContent

import com.log.data.ChatData
import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import java.time.OffsetDateTime

fun makeFakeChats(): List<ChatData> {
    val chats: MutableList<ChatData> = mutableListOf()

    val author1 = UserData(
        3,
        "puffer",
        "Соняша",
        "",
        "ICON"
    )
    val author2 = UserData(
        4,
        "krairox",
        "Матвей",
        "Который Н",
        "ICON"
    )
    val author3 = UserData(
        5,
        "averdroz",
        "Дима",
        "Чорни",
        "ICON"
    )
    val author4 = UserData(
        6,
        "menger",
        "Максим",
        "Решето",
        "ICON"
    )
    val author0 = UserData(
        1,
        "n1kromant",
        "Роман",
        "Ник",
        "ICON"
    )

    val content = Content(
        "Привет, лучший друг!",
        null
    )
    val content2 = Content(
        "Привет, лучший подруг!",
        null
    )

    val authors = listOf(author0, author1, author2, author3, author4)

    for (i in (1..4) ) {
        val messages: MutableList<Message> = mutableListOf()


        messages += Message(
            3,
            author = authors[i],
            dateTime = OffsetDateTime.now(),
            content = content,
            isReplyTo = null
        )
        messages += Message(
            4,
            author = authors[0],
            dateTime = OffsetDateTime.now(),
            content = content2,
            isReplyTo = null
        )
        messages += Message(
            5,
            author = authors[i],
            dateTime = OffsetDateTime.now(),
            content = content,
            isReplyTo = null
        )
        messages += Message(
            6,
            author = authors[0],
            dateTime = OffsetDateTime.now(),
            content = content2,
            isReplyTo = null
        )
        messages += Message(
            7,
            author = authors[i],
            dateTime = OffsetDateTime.now(),
            content = content,
            isReplyTo = null
        )

        val data = ChatData(
            chatId = i.toLong(),
            members = mutableListOf(0,i+1),
            title = authors[i].firstName + " " +authors[i].lastName,
            avatar = null,
            messages = messages
        )
        chats += data
    }
    return chats.toList()
}