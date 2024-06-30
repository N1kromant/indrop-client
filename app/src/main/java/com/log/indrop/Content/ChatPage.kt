package com.log.indrop.Content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.log.data.ChatData
import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import com.log.indrop.R
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@Composable
fun ChatPage(data: ChatData, myId: String, me: UserData, onClick: (task: String, metaData: String?) -> Unit) {
    Column {
        ChatHeader(data) { task, id ->  onClick(task, id) }
        ChatContent(data, myId, this)
        ChatFooter(me) { task, id -> onClick(task, id) }
    }
}

@Composable
fun ChatHeader(data: ChatData, onClick: (task: String, id: String?) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .verticalScroll(rememberScrollState())
    ) {
        Button(onClick = { onClick("goBack", null) }, Modifier.weight(0.3f)) {
            Icon(painter = painterResource(id = R.drawable.go_back), contentDescription = "goBack")
        }
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "avatar",
            Modifier
                .fillMaxSize()
                .align(Alignment.CenterVertically)
                .weight(0.2f)
                .clickable { onClick("goToPartnersProfile", null) }
        )

        Text(
            text = data.title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            fontSize = 28.sp
        )
    }
}

@Composable
fun ChatContent(chat: ChatData, myId: String, columnScope: ColumnScope) {
    columnScope.apply {
        LazyColumn (
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {

            items(chat.messages) {
                Message(it, it.author.login == myId)
                Spacer(Modifier.size(4.dp))
            }
        }
//        Column(
//            Modifier.weight(1f),
//            verticalArrangement = Arrangement.Bottom
//        ) {
//            messages.collectAsState().value!!.messages.forEach {
//                Message(it, it.author.login == myId)
//                Spacer(Modifier.size(4.dp))
//            }
//        }
    }
}

@Composable
fun Message(message: Message, isMyMessage: Boolean) {
    val shape = RoundedCornerShape(16.dp) // Форма с скругленными углами (здесь радиус - 8.dp)

    Row(
        Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer, shape)
            .fillMaxWidth(),
        horizontalArrangement = (if (isMyMessage) {Arrangement.End} else {Arrangement.Start}),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Row(
            Modifier
                .background(MaterialTheme.colorScheme.primaryContainer, shape)
        ) {
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = message.content.text ?: "", fontSize = 20.sp)
                Row(
//                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = message.dateTime.formatOffsetDateTime(), fontSize = 12.sp, fontWeight = FontWeight.Light)
                }
                Spacer(modifier = Modifier.size(4.dp))
            }
            Spacer(modifier = Modifier.size(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatFooter(me: UserData, onClick: (task: String, metaData: String?) -> Unit) {
    var text by remember { mutableStateOf("") }
    val textFieldColors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
    fun sendMessage() {
        val message = Message(
            messageId = null,
            author = me,
            content = Content(text, null),
            dateTime = OffsetDateTime.now(),
            isReplyTo = null
        )
        onClick("sendMessage", message.toJson())
        text = ""
    }
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer), verticalAlignment = Alignment.Bottom) {
        IconButton(onClick = { /*TODO*/ }, Modifier.weight(0.1f)) {
            Icon(painter = painterResource(id = R.drawable.paperclip), contentDescription = "Clip", tint = MaterialTheme.colorScheme.onPrimary)
        }
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            label = { Text("Сообщение") },
            colors = textFieldColors,
            modifier = Modifier.weight(0.8f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Default,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onSend = { sendMessage() }
            )
        )
        IconButton(
            onClick = { sendMessage() },
            Modifier.weight(0.1f)) {
            Icon(painter = painterResource(id = R.drawable.send), contentDescription = "Send", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Preview
@Composable
fun ChatPagePreview() {
    val messages: MutableList<Message> = mutableListOf()

    val author = UserData(
        1,
        "puffer",
        "Соняша",
        "Меньше чем три",
        "ICON"
    )
    val me = UserData(
        0,
        "n1kromant",
        "Roman",
        "Nikolaev",
        "ICON"
    )

    val content = Content(
        "Привет, лучший друг!",
        null
    )

    messages += Message(
        0,
        author = author,
        dateTime = OffsetDateTime.now(),
        content = content,
        isReplyTo = null
    )
    messages += Message(
        0,
        author = me,
        dateTime = OffsetDateTime.now(),
        content = content,
        isReplyTo = null
    )

    val data = ChatData(
        1,
        members = mutableListOf(0, 1),
        title = "Соняша XXXXXXXXX",
        avatar = null,
        messages = messages
    )

//    InkTheme {
//        ChatPage(data.collectAsState().value!!, "n1kromant", me) { _, _ ->
//
//        }
//    }
}