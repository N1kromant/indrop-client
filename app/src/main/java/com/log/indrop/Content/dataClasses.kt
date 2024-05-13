package com.log.indrop.Content

import android.net.Uri
import java.time.OffsetDateTime

data class Content(
    var text: String?,
    val images: List<Uri>?,
) {

}

data class UserData(
    val authorId: String,
    val firstName: String,
    val lastName: String,
    val icon: String, //Url
) {

}

data class PostData(
    val postId: Long,
    val author: UserData,
    val dateTime: OffsetDateTime,
    val content: Content,
) {

}

data class ChatData(
    val chatId: Long,
    val members: MutableList<Int>,
    val avatar: Uri?,
    val title: String,
    val messages: List<Message>,
) {
    fun getLastMessage(): Message {
        return messages[0]
    }
}

data class Message(
    val messageId: Long,
    val author: UserData,
    val dateTime: OffsetDateTime,
    val content: Content,
    val isReplyTo: Boolean
) {

}