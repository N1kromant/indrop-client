package com.log.network

import com.log.data.AuthorizationResponse
import com.log.data.ChatData
import com.log.data.Comment
import com.log.data.Message
import com.log.data.PostData
import com.log.data.UserData
import com.log.network.Interfaces.ChatNetwork
import com.log.network.Interfaces.PostsNetwork
import com.log.network.Interfaces.UserNetwork
import io.ktor.client.*
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
object NetworkManager:
    PostsNetwork,
    ChatNetwork,
    UserNetwork {
    private lateinit var client: HttpClient
    private lateinit var me: UserData

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

    override fun send(message: Message) {
        TODO("Not yet implemented")
    }
    override fun send(message: Comment) {
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
//    fun connect() {
//        client = HttpClient {
//            install(WebSockets)
//        }
//        runBlocking {
//            client.webSocket(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {
//
//                val userInputRoutine = launch { inputMessages() }
//                val messageOutputRoutine = launch { outputMessages() }
//
//                userInputRoutine.join() // Wait for completion; either "exit" or error
////                messageOutputRoutine.cancelAndJoin()
////
//            }
//        }
//        println("Connection closed. Goodbye!")
//    }
//
//    suspend fun DefaultClientWebSocketSession.inputMessages() {
//        try {
//            for (message in incoming) {
//
//                val textFrame = message as Frame.Text
//                val m = textFrame.readText()
//                val userData = Json.decodeFromString(UserDataSerializer, m)
//
//                println(userData.login)
////                Log.d("network", userData.login)
//            }
//        } catch (e: Exception) {
//            println("Error while receiving: " + e.localizedMessage)
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
//    suspend fun DefaultClientWebSocketSession.outputMessages() {
//        while (true) {
//            try {
//                if (data != "") {
//                    send(data)
//                    data = ""
//                }
//            } catch (e: Exception) {
//                println("Error while sending: " + e.localizedMessage)
//            }
//
//        }
//
//    }
}


