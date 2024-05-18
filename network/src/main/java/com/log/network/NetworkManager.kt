package com.log.network

import android.util.Log
import com.log.data.UserData
import com.log.data.UserDataSerializer
import io.ktor.client.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@OptIn(DelicateCoroutinesApi::class)
object NetworkManager {
    private lateinit var client: HttpClient
    var data = ""

    fun connect() {
        client = HttpClient {
            install(WebSockets)
        }
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {

                val userInputRoutine = launch { inputMessages() }
                val messageOutputRoutine = launch { outputMessages() }

                userInputRoutine.join() // Wait for completion; either "exit" or error
//                messageOutputRoutine.cancelAndJoin()
//
            }
        }
        println("Connection closed. Goodbye!")
    }

    suspend fun DefaultClientWebSocketSession.inputMessages() {
        try {
            for (message in incoming) {

                val textFrame = message as Frame.Text
                val m = textFrame.readText()
                val userData = Json.decodeFromString(UserDataSerializer, m)

                println(userData.login)
//                Log.d("network", userData.login)
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }

    suspend fun DefaultClientWebSocketSession.outputMessages() {
        while (true) {
            try {
                if (data != "") {
                    send(data)
                    data = ""
                }
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
            }

        }
    //        while (true) {
//            val message = readLine() ?: ""
//            if (message.equals("exit", true)) return
//
//
//            if (message != "") {
//
//                try {
//                    send(message)
//                } catch (e: Exception) {
//                    println("Error while sending: " + e.localizedMessage)
//                    return
//                }
//            }
//        }
    }
}

//    fun send(message: String) {
//        try {
//            runBlocking {
//                client.webSocketSession(method = HttpMethod.Get, host = "192.168.1.88", port = 8080, path = "/chat") {
//                    send(message)
//                }
//            }
////            send(message)
//        } catch (e: Exception) {
//            println("Error while sending: " + e.localizedMessage)
//            return
//        }
//    }

//}
