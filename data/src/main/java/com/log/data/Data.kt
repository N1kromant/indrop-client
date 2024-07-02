package com.log.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import java.time.OffsetDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json

@Serializable
data class Content(
    var text: String?,
    val images: List<String>?,
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

data class PostData(
    val postId: Long,
    val author: UserData,
    val dateTime: OffsetDateTime,
    val content: Content,
    val comments: List<Message>?,
)

data class Comment(
    val id: Long?,
    val postId: Long,
    val content: Message,
    var likesCount: Int
)

@Serializable
data class ChatData(
    val chatId: Long,
    val members: MutableList<Int>,
    val avatar: String?,
    val title: String,
    var messages: List<Message>,
) {
    fun getLastMessage(): Message {
        return messages[0]
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