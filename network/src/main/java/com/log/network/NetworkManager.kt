package com.log.network

import androidx.lifecycle.Observer
import com.log.data.AuthorizationResponse
import com.log.data.ChatData
import com.log.data.Comment
import com.log.data.Message
import com.log.data.PostData
import com.log.data.UserData
import com.log.indrop.MainViewModel
import com.log.network.Interfaces.ChatNetwork
import com.log.network.Interfaces.PostsNetwork
import com.log.network.Interfaces.UserNetwork
import com.log.network.ViewModels.NetworkViewModel
import io.ktor.client.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
@OptIn(DelicateCoroutinesApi::class)
class NetworkManager(mainViewModel: com.log.indrop.MainViewModel) :
    PostsNetwork,
    ChatNetwork,
    UserNetwork {
    private lateinit var client: HttpClient
    private lateinit var me: UserData
    private val HOST: String = "192.168.1.88"
    private val viewModel: NetworkViewModel = NetworkViewModel()
    private lateinit var inputObserver: Observer<Message>
    private lateinit var outputObserver: Observer<Message>

    fun login(
        login: String,
        password: String
    ): AuthorizationResponse {
        TODO("Not yet implemented")
    }
    fun register(
        login: String,
        password: String,
        firstName: String,
        lastName: String
    ): AuthorizationResponse {
        TODO("Not yet implemented")
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

    override fun getAllChats(): List<ChatData> {
        TODO("Not yet implemented")
    }

    override fun getNewChat(): ChatData {
        TODO("Not yet implemented")
    }

    override fun DefaultClientWebSocketSession.sendData(message: Message) {

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
fun connect() {
    client = HttpClient {
        install(WebSockets)
    }
//    inputObserver = Observer {
//        sendData(it)
//    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = HOST, port = 8080, path = "/messages") {
            outputObserver = Observer {
                sendData(it)
            }
            viewModel.newInputMessage.observeForever(inputObserver)
//                val serializedMessage = Json.encodeToString(Message.serializer(), message)
//                val serializedMessage = Json.encodeToString<Message>(message)
//                val serializedMessage = Json.encodeToString(message)
//                val serializedMessagee = Json.encode
//                send(Frame.Binary(message))
//                sendSerialized(message)
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()


            }
//            val input = launch { inputMessages() }
//            launch { outputMessages() }
//                send(Frame.Text(serializedMessage))
//            input.join()
//            output.cancelAndJoin()
            observerClear()
        }
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



                viewModel.newInputMessage.value = data
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

    fun observerClear() {
//        if (this::observer.isInitialized)
            viewModel.newInputMessage.removeObserver(inputObserver)
    }

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


