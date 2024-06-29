package com.log.indrop

import android.os.Looper
import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import com.log.network.NetworkManager
import com.log.network.ViewModels.MainViewModel
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


import java.time.OffsetDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//
//@RunWith(MockitoJUnitRunner.class)
//class NetworkUnitTest {
//    @Test
//    fun addition_isCorrect() {
//
//
//        assertEquals(4, 2 + 2)
//    }
//
//    @Test
//    fun networkTest() {
//        mockkStatic(Looper::class)
//        every { Looper.getMainLooper() } returns mockk()
//
//        val mainViewModel = MainViewModel()
//
//        val net = NetworkManager(mainViewModel)
//        net.connect()
//
//        val author = UserData(
//            authorId = null,
//            login = "n1kromant",
//            firstName = "Роман",
//            lastName = "Николаев",
//            icon = null
//        )
//
//        val msg = Message(
//            messageId = null,
//            author = author,
//            dateTime = OffsetDateTime.now(),
//            content = Content("He11o w0rld!", null),
//            isReplyTo = null
//        )
//
//        net.viewModel.newOutputMessage.value = msg.toJson()
//
////        net.viewModel.newInputMessage
////        net.sendData(msg)
//
////        assertEquals(4, 2 + 2)
//    }
//}

