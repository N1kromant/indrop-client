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

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.*
import kotlinx.serialization.json.JsonElement

import android.util.Log

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
        const val SERVER = "api.1ndrop.ru"
    }
    private val select = HOST.SERVER

    private val port = ":4444"
    private val url = select
    private val uri = "http://$select$port"
    private val graphqlUrl = "$uri/graphql"

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

    suspend fun loginTry(
        login: String,
        password: String
    ): AuthorizationResponse {
        val graphqlQueryAuth = """
        mutation {
            authenticateUser(login: "$login", password: "$password") {
                success
                token
            }
        }
    """.trimIndent()



        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true  // Игнорируем незнакомые ключи
                    isLenient = true  // Разрешаем более гибкое парсинг
                    coerceInputValues = true // Преобразовывать входные значения в null, если они не могут быть преобразованы
                    encodeDefaults = true  // Кодировать даже значения по умолчанию
                })
            }
        }

        var authResponse: AuthorizationResponse

        try {
            withTimeout(5000) {
                val response: HttpResponse = withContext(Dispatchers.IO) {
                    client.post(graphqlUrl) {
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("query" to graphqlQueryAuth))
                    }
                }

                println("Статус ответа: ${response.status}")
                println("Тело ответа: ${response.bodyAsText()}")

                if (response.status == HttpStatusCode.OK) {
                    println("Статус ответа: ${response.status}")
                    val responseBody = response.bodyAsText()  // Получаем тело ответа как строку
                    println("Тело ответа: $responseBody")

                    // Десериализуем JSON в объект AuthorizationResponse
                    authResponse = Json.decodeFromString(responseBody)
                }
                else {
                    authResponse = AuthorizationResponse(data = AuthenticateUser(AuthenticateUserResponse(success = false, token = null)))
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("Ответ не был получен в течение 10 секунд")
            authResponse = AuthorizationResponse(data = AuthenticateUser(AuthenticateUserResponse(success = false, token = null)))
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            authResponse = AuthorizationResponse(data = AuthenticateUser(AuthenticateUserResponse(success = false, token = null)))
        } finally {
            client.close()
        }


        return authResponse
    }



    suspend fun registerTry(
        login: String,
        password: String,
        firstName: String,
    ): RegistrationResponse {
        val graphqlQueryRegister = """
    mutation {
        addUser(input: {
            login: "$login",
            firstName: "$firstName",
            lastName: "$firstName",
            password: "$password"
        }) {
            success
            AddUserResponseUserData {
                login
                password
                name
            }
        }
    }
""".trimIndent()

        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true  // Игнорируем незнакомые ключи
                    isLenient = true  // Разрешаем более гибкое парсинг
                    coerceInputValues = true // Преобразовывать входные значения в null, если они не могут быть преобразованы
                    encodeDefaults = true  // Кодировать даже значения по умолчанию
                })
            }
        }

        var registerResponse: RegistrationResponse

        try {
            withTimeout(10000) {
                val response: HttpResponse = withContext(Dispatchers.IO) {
                    client.post(graphqlUrl) {
                        contentType(ContentType.Application.Json)
                        setBody(mapOf("query" to graphqlQueryRegister))
                    }
                }

                println("Статус ответа: ${response.status}")
                println("Тело ответа: ${response.bodyAsText()}")

                if (response.status == HttpStatusCode.OK) {
                    println("Статус ответа: ${response.status}")
                    val responseBody = response.bodyAsText()  // Получаем тело ответа как строку
                    Log.i("MyApp","Тело ответа: $responseBody")

                    registerResponse = Json.decodeFromString(responseBody)

                }
                else {
                    registerResponse = RegistrationResponse(data = RegistrateUser(AddUserResponse(success = false,AddUserResponseUserData = null)))
                }
            }
        } catch (e: TimeoutCancellationException) {
            println("Ответ не был получен в течение 10 секунд")
            registerResponse = RegistrationResponse(data = RegistrateUser(AddUserResponse(success = false, AddUserResponseUserData = null)))
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            registerResponse = RegistrationResponse(data = RegistrateUser(AddUserResponse(success = false, AddUserResponseUserData = null)))
        } finally {
            client.close()
        }

        return registerResponse
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


