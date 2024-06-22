package com.log.indrop

import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import com.log.network.NetworkManager
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
    fun networkTest() {
        val net = NetworkManager(mainViewModel)
        net.connect()

        val author = UserData(
            authorId = null,
            login = "n1kromant",
            firstName = "Роман",
            lastName = "Николаев",
            icon = null
        )

        val msg = Message(
            messageId = null,
            author = author,
            dateTime = OffsetDateTime.now(),
            content = Content("He11o w0rld!", null),
            isReplyTo = null
        )

//        net.sendData(msg)

//        assertEquals(4, 2 + 2)
    }
}

