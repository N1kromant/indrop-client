package com.log.indrop.Content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.log.data.ChatData
import com.log.data.Content
import com.log.data.Message
import com.log.data.UserData
import com.log.indrop.FakeContent.makeFakeChats
import com.log.indrop.R
import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.indrop.ViewModels.MessagesViewModel.MessagesEffect
import com.log.indrop.ViewModels.MessagesViewModel.MessagesIntent
import com.log.indrop.ViewModels.MessagesViewModel.MessagesViewModel
import com.log.indrop.ui.theme2.InkTheme
import com.log.indrop.ViewModels.Search.SearchIntent
import com.log.indrop.ViewModels.Search.SearchViewModel
import com.log.indrop.api.SearchApiImpl
import com.log.indrop.api.SearchApiTestImpl
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MessagesPage(messagesViewModel: MessagesViewModel = koinInject<MessagesViewModel>(), chats: List<ChatData>, navController: NavController, onClickChat: (chatData: ChatData) -> Unit) {


    LaunchedEffect(messagesViewModel) {
        messagesViewModel.effect.collect { effect ->
            when (effect) {
                is MessagesEffect.RouteToSearch -> {
                    navController.navigate("search") {
                        popUpTo("messages")
                    }
                }
            }
        }
    }



    Column {
        Row (
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = stringResource(R.string.MessagesPageTitle),
                fontSize = 36.sp,
                modifier = Modifier
                    .padding(
                        vertical = 12.dp,
                        horizontal = 12.dp
                    )
                    .align(Alignment.CenterVertically)            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            IconButton(
                onClick = { messagesViewModel.processIntent(MessagesIntent.SearchButtonPressed) },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_search),
                    "search",
                    tint = colorResource(R.color.white)
                )
            }
        }
        Spacer(modifier = Modifier.size(2.dp))

        LazyColumn {
            items(chats) {
                ChatRow(it) { chatData ->
                    onClickChat(chatData)
                }
                Spacer(modifier = Modifier.size(2.dp))
            }
        }
    }
}

//@Preview
//@Composable
//fun MessagesPagePreview() {
//    val fakeChats = makeFakeChats()
//    InkTheme {
//        MessagesPage(MessagesViewModel(), fakeChats)
//    }
//}

@Composable
fun ChatRow(data: ChatData, onClick: (chatData: ChatData) -> Unit) {

    Column (
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClick(data) }
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .weight(0.3f)
        )
        Row(
            modifier = Modifier
                .weight(1f)

        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Avatar",
                Modifier
                    .fillMaxSize()
                    .weight(0.2f)
            )
            Column (Modifier.weight(0.8f)) {
                Text(
                    text = data.title,
                    fontSize = 28.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row {
                    Text(text = "${data.getLastMessage()?.author?.firstName}: ")
                    Text(text = data.getLastMessage()?.content?.text ?: "")
                }
            }
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    modifier = Modifier
                        .weight(0.1f)
                        .padding(8.dp),
                    text = if (data.getLastMessage()?.dateTime != null) {
                        val messageTime = data.getLastMessage()?.dateTime?.atZoneSameInstant(ZoneId.systemDefault())
                        messageTime?.let { formatMessageDateTime2(it) } ?: ""
                    } else {
                        ""  // или любой другой текст по умолчанию
                    },
                    fontSize = 18.sp,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .weight(0.3f)
        )
    }
}

fun formatMessageDateTime2(messageDateTime: ZonedDateTime): String {
    val now = ZonedDateTime.now()
    val today = now.toLocalDate()
    val yesterday = today.minusDays(1)
    val messageDate = messageDateTime.toLocalDate()

    return when {
        // Если сообщение сегодня - показываем только время
        messageDate.isEqual(today) -> {
            messageDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        // Если сообщение вчера - пишем "Вчера" и время
        messageDate.isEqual(yesterday) -> {
            "Вчера, " + messageDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        // Если в этом году - день, месяц и время
        messageDate.year == today.year -> {
            messageDateTime.format(DateTimeFormatter.ofPattern("d MMM, HH:mm"))
        }

        // Если другой год - полная дата со временем
        else -> {
            messageDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
        }
    }
}

@Preview
@Composable
fun ChatPreview() {
    val messages: MutableList<Message> = mutableListOf()

    val author = UserData(
        2,
        "Krairox",
        "Матвей",
        "Который Н",
        "ICON"
    )

    val content = Content(
        "Привет, лучший друг!",
        null
    )

    messages += Message(
        10,
        author = author,
        dateTime = OffsetDateTime.now(),
        content = content,
        isReplyTo = null
    )

    val data = ChatData(
        2,
        mutableListOf(0,2),
        title = "Матвей Который Н",
        avatar = null,
        messages = messages
    )

    InkTheme {
        ChatRow(data) {}
//    data.messages[0].author.firstName = "Соняша"
//    data.messages[0].author.lastName = "<3"
        ChatRow(data) {}
        ChatRow(data) {}
    }
}

