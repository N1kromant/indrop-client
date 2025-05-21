package com.log.data

import kotlinx.serialization.KSerializer
import java.time.OffsetDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class Content(
    var text: String?,
    val images: String?,
) {

}
@Serializable
data class UserData(
    val authorId: Long?,
    val login: String,
    val firstName: String,
    val lastName: String,
    val icon: String?, //Url
)

interface Likeable {
    val postId: Long
    var likesCount: Int
    var isLiked: Boolean
}

data class PostData(
    override val postId: Long,
    val author: UserData,
    val dateTime: OffsetDateTime,
    val content: Content,
    val comments: List<Comment> = emptyList<Comment>(),
    override var likesCount: Int = 0,
    override var isLiked: Boolean = false
) : Likeable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PostData) return false
        return postId == other.postId
    }

    override fun hashCode(): Int {
        return postId.hashCode()
    }
}

data class Comment(
    val id: Long?,
    val content: Message,
    override val postId: Long,
    override var likesCount: Int = 0,
    override var isLiked: Boolean = false
) : Likeable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Comment) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

@Serializable
data class ChatData(
    val chatId: Long,
    val members: MutableList<Int>,
    val avatar: String?,
    val title: String,
    var messages: List<Message>,
) {
    fun getLastMessage(): Message? {
        return messages.lastOrNull()
    }
}

data class AuthorizationResponse(
    var result: Boolean,
    var user: UserData
)

@Serializable
data class Message(
    val messageId: Long?,
    val author: UserData,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val dateTime: OffsetDateTime,
    val content: Content,
    val isReplyTo: Long?
) {
    fun toJson(): String {
        return Json.encodeToString(this)
    }
}
object OffsetDateTimeSerializer : KSerializer<OffsetDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: OffsetDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): OffsetDateTime {
        return OffsetDateTime.parse(decoder.decodeString())
    }
}