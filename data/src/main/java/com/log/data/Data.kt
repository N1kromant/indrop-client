package com.log.data

import com.sun.jna.platform.unix.solaris.LibKstat.KstatNamed.UNION.STR
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import java.time.OffsetDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import org.jdom.filter2.Filters.element


data class Content(
    var text: String?,
    val images: List<String>?,
) {

}
@Serializable
data class UserData(
    val authorId: Long,
    val login: String,
    val firstName: String,
    val lastName: String,
    val icon: String?, //Url
)
object UserDataSerializer : KSerializer<UserData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("UserData") {
        element<Long>("authorId")
        element<String>("login")
        element<String>("firstName")
        element<String>("lastName")
        element<String?>("icon")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: UserData) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeLongElement(descriptor, 0, value.authorId)
        composite.encodeStringElement(descriptor, 1, value.login)
        composite.encodeStringElement(descriptor, 2, value.firstName)
        composite.encodeStringElement(descriptor, 3, value.lastName)
        composite.encodeNullableSerializableElement(descriptor, 4, String.serializer().nullable, value.icon)
        composite.endStructure(descriptor)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): UserData {
        var authorId = 0L
        var login = ""
        var firstName = ""
        var lastName = ""
        var icon: String? = null

        decoder.decodeStructure(descriptor) {
            loop@ while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break@loop
                    0 -> authorId = decodeLongElement(descriptor, index)
                    1 -> login = decodeStringElement(descriptor, index)
                    2 -> firstName = decodeStringElement(descriptor, index)
                    3 -> lastName = decodeStringElement(descriptor, index)
                    4 -> icon = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                    else -> throw SerializationException("Unknown index: $index")
                }
            }
        }
        return UserData(authorId, login, firstName, lastName, icon)
    }
}

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

data class ChatData(
    val chatId: Long,
    val members: MutableList<Int>,
    val avatar: String?,
    val title: String,
    val messages: List<Message>,
) {
    fun getLastMessage(): Message {
        return messages[0]
    }
}

data class AuthorizationResponse(
    var result: Boolean,
    var user: UserData
)

data class Message(
    val messageId: Long?,
    val author: UserData,
    val dateTime: OffsetDateTime,
    val content: Content,
    val isReplyTo: Long?
) {

}