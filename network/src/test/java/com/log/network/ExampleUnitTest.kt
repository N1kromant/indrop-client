package com.log.network

import android.util.Log
import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import com.log.network.ViewModels.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

import org.junit.Assert.*
import java.time.OffsetDateTime

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

    @Test
    fun makeMessage() {
        val me = UserData(
            1,
            "n1kromant",
            "Роман",
            "Николаев",
            "ICON"
        )

        val message = Message(
            messageId = null,
            author = me,
            content = Content("Text", null),
            dateTime = OffsetDateTime.now(),
            isReplyTo = null
        )
        println(Json.encodeToString(message))
    }
 }

@OptIn(DelicateCoroutinesApi::class)
class NetworkManagerUnitTest {
    @Test
    fun mainTest() {
//        val manager = NetworkManager
//        manager.connect()
//        runBlocking {
//            manager.send("g")
//        }
    }

    @Test
    fun getChatsTest() {
        val manager = NetworkManager()

        runBlocking {
            manager.connect()
        }
        val l = manager.getAllChats(1)

        print(l)

    }
}
