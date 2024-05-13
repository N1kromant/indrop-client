package com.log.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

@OptIn(DelicateCoroutinesApi::class)
class NetworkManagerUnitTest {
    @Test
    fun mainTest() {
        val client = HttpClient {
            install(WebSockets)
        }
        runBlocking {
            client.webSocket(method = HttpMethod.Get, host = "192.168.1.59", port = 8080, path = "/chat") {
                val messageOutputRoutine = launch { outputMessages() }
                val userInputRoutine = launch { inputMessages() }

                userInputRoutine.join() // Wait for completion; either "exit" or error
                messageOutputRoutine.cancelAndJoin()
            }
        }
        client.close()
        println("Connection closed. Goodbye!")
    }

    private suspend fun DefaultClientWebSocketSession.outputMessages() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                println(message.readText())
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }

    private suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            val message = readLine() ?: ""

            if (message.equals("exit", true)) return
            try {
                send(Frame.Text(message))
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
                return
            }
        }
    }
}
//    fun getResponse() {
//        val client = HttpClient(CIO)
//        val response = runBlocking {
//            client.get("https://ktor.io/")
//        }
//    }
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
//}