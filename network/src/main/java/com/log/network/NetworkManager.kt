package com.log.network

import androidx.lifecycle.Observer
import com.log.data.ChatData
import com.log.data.Comment
import com.log.data.Message
import com.log.data.PostData
import com.log.data.UserData
import com.log.network.Interfaces.ChatNetwork
import com.log.network.Interfaces.PostsNetwork
import com.log.network.Interfaces.UserNetwork
import com.log.network.ViewModels.MainViewModel
import com.log.network.ViewModels.NetworkViewModel
import io.ktor.client.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

import java.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement
import android.util.Log

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.http.DefaultHttpEngine
import com.apollographql.apollo3.network.ws.GraphQLWsProtocol
import com.apollographql.apollo3.network.ws.WebSocketNetworkTransport
import com.example.graphql.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime




@OptIn(DelicateCoroutinesApi::class)
class NetworkManager() :
    PostsNetwork,
    ChatNetwork,
    UserNetwork {
    private lateinit var client: HttpClient
    private lateinit var me: UserData
//    private val HOST: String = "192.168.1.88"
    public val viewModel: NetworkViewModel = NetworkViewModel() //FIXME
    private lateinit var inputObserver: Observer<String>
    private lateinit var outputObserver: Observer<String>

    companion object HOST {
        const val HOME = "192.168.1.88"
        const val COUNTRY = "192.168.2.152"
        const val OWN = "127.0.0.1"
        const val SERVER1 = "api.1ndrop.ru"
        const val SERVER2 = "212.67.13.82"
    }
    private val select = HOST.SERVER2

    private val port = ":4000"
    private val url = select
    private val uri = "http://$select$port"
    private val graphqlUrl = "$uri/graphql"

    val graphqlUrlTEST = "http://$select$port/graphql"
    val graphqlUrlTESTws = "ws://$select$port/graphql"



    val apolloClient = ApolloClient.Builder()
        .serverUrl(graphqlUrlTEST)
        .subscriptionNetworkTransport(
            WebSocketNetworkTransport.Builder()
                .serverUrl(graphqlUrlTESTws)
                .protocol(GraphQLWsProtocol.Factory())
                .build()
        )
        .httpEngine(DefaultHttpEngine())
        .build()

    data class ChatCreationResult(val success: Boolean, val chatId: Int? = null)




    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class AuthorizationResponse(
        @SerialName("data") // Указываем точное имя поля в JSON
        val data: AuthenticateUser // Вложенные данные в поле "data"
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class AuthenticateUserResponse(
        val success: Boolean,
        val token: String? // Токен может быть null
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class AuthenticateUser(
        val authenticateUser: AuthenticateUserResponse
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class RegistrationResponse(
        @SerialName("data") // Указываем точное имя поля в JSON
        val data: RegistrateUser // Вложенные данные в поле "data"
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class RegistrateUser(
        val addUser: AddUserResponse
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class AddUserResponse(
        val success: Boolean,
        val AddUserResponseUserData: AddUserResponseUserData? // Может быть null, если добавление не удалось
    )

    @OptIn(InternalSerializationApi::class)
    @Serializable
    data class AddUserResponseUserData(
        val login: String,
        val password: String,
        val name: String
    )


    @Serializable
    data class loginResponse(
        val success: Boolean,
        val token: String?,
        val userId: Long?
    )

    // Функция для регистрации нового пользователя
    suspend fun registerUser(client: ApolloClient = apolloClient, login: String, password: String, firstName: String): Boolean {
        try {
            val mutation = AddUserMutation(
                login = login,
                firstName = firstName,
                lastName = firstName,
                password = password
            )

            val response = client.mutation(mutation).execute()

            if (response.hasErrors()) {
                println("Ошибка при регистрации: ${response.errors}")
                return false
            }

            return response.data?.addUser?.success == true
        } catch (e: Exception) {
            println("Исключение при регистрации: ${e.message}")
            return false
        }
    }

    // Функция для авторизации пользователя
    suspend fun loginUser(client: ApolloClient = apolloClient, login: String, password: String): loginResponse {
        try {
            val mutation = AuthenticateUserMutation(
                login = login,
                password = password
            )

            val response = client.mutation(mutation).execute()

            if (response.hasErrors()) {
                println("Ошибка при авторизации: ${response.errors}")
                return loginResponse(success = false, token = null, userId = null)
            }
            return if(response.data?.authenticateUser?.success != null) {
                if(response.data?.authenticateUser?.success == true) {
                    loginResponse(success = true, token = response.data?.authenticateUser?.token, userId = response.data?.authenticateUser?.userId?.toLong())
                } else loginResponse(success = false, token = null, userId = null)
            } else loginResponse(success = false, token = null, userId = null)

        } catch (e: Exception) {
            println("Исключение при авторизации: ${e.message}")
            return loginResponse(success = false, token = null, userId = null)
        }
    }



    override fun setPost(post: PostData) {
        TODO("Not yet implemented")
    }

    override fun setLike(post: PostData) {
        TODO("Not yet implemented")
    }

    override fun getNewMessages(): List<Message> {
        TODO("Not yet implemented")
    }

    override fun getAllChats(userId: Long): List<ChatData> {
        lateinit var response: String
        val localUrl = uri + "/getChats/${userId}"

        runBlocking {
            launch { response = client.get(localUrl).bodyAsText() }
        }

        return Json.decodeFromString<MutableList<ChatData>>(response)
    }

    override fun getNewChat(): ChatData {
        TODO("Not yet implemented")
    }

    override fun DefaultClientWebSocketSession.sendData(message: String) {
        runBlocking {
            send(Frame.Text(message))
        }
    }

//    fun sendData(message: Msg) {
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = HOST, port = 8080, path = "/messages") {
////                val serializedMessage = Json.encodeToString(Message.serializer(), message)
////                val serializedMessage = Json.encodeToString<Message>(message)
//                val serializedMessage = Json.encodeToString(message)
////                val serializedMessagee = Json.encode
////                send(Frame.Binary(message))
////                sendSerialized(message)
//                send(Frame.Text(serializedMessage))
//            }
//        }
//    }
    override fun sendData(message: Comment) {
        TODO("Not yet implemented")
    }
    override fun setNewChat(chat: ChatData) {
        TODO("Not yet implemented")
    }

    override fun setStatus(status: String) {
        TODO("Not yet implemented")
    }

    override fun setAvatar(avatar: String) {
        TODO("Not yet implemented")
    }


//    var data = ""
//
    suspend fun connect() {
        client = HttpClient {
            install(WebSockets)
        }
    }

    suspend fun openChat(chatId: Long) {
        client.webSocket(method = HttpMethod.Get, host = url, port = port.substring(1).toInt(), path = "/chat/$chatId") {
            viewModel.newOutputMessage.collect { message ->
                message?.let {
                    sendData(
                        message
                    )
                }
            }
//                launch {
//                    for (frame in incoming) {
//                        frame as? Frame.Text ?: continue
//                        val receivedText = frame.readText()
//
//                    }
//                }
        }

    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        try {
//            observer = Observer {
//                sendData(it)
//            }
            for (message in incoming) {

                val textFrame = message as Frame.Text
                val m = textFrame.readText()
                val data = Json.decodeFromString<Message>(m)



//                viewModel.newInputMessage.value = data
//                val userData = Json.decodeFromString(UserDataSerializer, m)

//                println(data.content.text)
//                Log.d("network", userData.login)
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }
    suspend fun DefaultClientWebSocketSession.outputMessages(data: String) {
        try {
            send(Frame.Text(data))
//                val serializedMessage = Json.encodeToString(message)
//                if (data != "") {
//                    send(data)
//                    data = ""
//                }
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
        }
//        while (true) {
//
//
//        }

    }

//    suspend fun observerClear() {
////        if (this::observer.isInitialized)
//        withContext(Dispatchers.Main) {
//            viewModel.newOutputMessage.removeObserver(outputObserver)
//        }
//    }

//
//    fun send(message: Message) {
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {
//                send(Json.encodeToString(message))
//            }
//        }
//    }
//    fun send(user: UserData) {
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {
//                send(Json.encodeToString(user))
//            }
//        }
//    }
//    fun send(post: PostData) {
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {
//                send(Json.encodeToString(post))
//            }
//        }
//    }
//
}


