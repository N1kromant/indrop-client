package com.log.network

import android.util.Log
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
//        val manager = NetworkManager
//        manager.connect()
//        runBlocking {
//            manager.send("g")
//        }
    }

    @Test
    fun getChatsTest() {
        val manager = NetworkManager(mainViewModel = MainViewModel())

        runBlocking {
            manager.connect()
        }
        val l = manager.getAllChats(1)

        print(l)

    }
}
