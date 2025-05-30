package com.log.indrop.Content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.log.indrop.ui.theme2.InkTheme
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import java.time.ZonedDateTime
import coil.compose.AsyncImage
import java.time.temporal.ChronoUnit

@Composable
fun ChatPage(data: ChatData, myId: String, me: UserData, onClick: (task: String, metaData: String?) -> Unit) {
    LaunchedEffect(Unit) {
        onClick("startSubscription", null)
    }
    DisposableEffect(Unit) {
        println("DisposableEffect initialized") // or use Log.d
        onDispose {
            println("onDispose being called") // or use Log.d
            onClick("cancelSubscription", null)
        }
    }

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
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(8.dp)
    ) {
        IconButton(onClick = { onClick("goBack", null)}) {
            Icon(painter = painterResource(id = R.drawable.go_back), contentDescription = "goBack", tint = MaterialTheme.colorScheme.onPrimary)
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
            fontSize = 28.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
    // Линия под Row
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onPrimary.copy()
    )
}

@Composable
fun ChatContent(chat: ChatData, myId: String, columnScope: ColumnScope) {
    val listState = rememberLazyListState()
    val messages = chat.messages

    // Автоскролл при добавлении новых сообщений
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0) // Скроллим к первому (самому новому) элементу
        }
    }

    val reversedMessages = messages.reversed()

    columnScope.apply {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            reverseLayout = true
        ) {
            itemsIndexed(reversedMessages) { index, message ->
                val previous = reversedMessages.getOrNull(index + 1)
                Message(message, message.author.authorId == myId.toLong(), previous)
            }
        }
    }
}

@Composable
fun Message(message: Message, isMyMessage: Boolean, previous: Message? = null) {
    val shape = RoundedCornerShape(16.dp)
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val minWidth = screenWidth * 0.3f    // Минимум 30% экрана
    val maxWidth = screenWidth * 0.7f     // Максимум 70% экрана

    // Определяем, нужно ли показывать имя автора
    val shouldShowAuthorName = previous == null ||
            previous.author.login != message.author.login ||
            shouldShowTimeGap(previous.dateTime, message.dateTime)

    // Определяем, нужно ли показывать аватар
    val shouldShowAvatar = previous == null ||
            previous.author.login != message.author.login

    Row(
        Modifier
            .fillMaxWidth()  // Занимаем всю доступную ширину для выравнивания
            .padding(horizontal = 8.dp, vertical = 1.dp),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start,
    ) {
        // Аватар пользователя для чужих сообщений (слева)
        if (!isMyMessage) {
            if (shouldShowAvatar) {
                UserAvatar(message.author.icon, message.author.firstName)
                Spacer(modifier = Modifier.size(8.dp))
            } else {
                // Добавляем отступ вместо аватара для выравнивания
                Spacer(modifier = Modifier.size(44.dp)) // 36dp (размер аватара) + 8dp (отступ)
            }
        }

        Column(
            // Ограничиваем ширину сообщения между min и max
            modifier = Modifier.widthIn(min = minWidth, max = maxWidth),
            // Добавляем выравнивание для всего содержимого колонки
            horizontalAlignment = if (isMyMessage) Alignment.End else Alignment.Start
        ) {
            // Имя автора сообщения с соответствующим выравниванием
            if (shouldShowAuthorName) {
                Text(
                    text = message.author.firstName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (isMyMessage) TextAlign.End else TextAlign.Start
                )
            }

            // Содержимое сообщения
            Surface(
                shape = shape,
                color = backgroundColor,
                modifier = Modifier.wrapContentWidth() // Контейнер сообщения подстраивается под контент
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    // Текст сообщения
                    Text(
                        text = message.content.text ?: "",
                        fontSize = 16.sp
                    )
                    // Время и логин отправителя
                    Row(
                        modifier = Modifier.align(if (isMyMessage) Alignment.End else Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val zonedDateTime = message.dateTime.atZoneSameInstant(ZoneId.systemDefault())
                        val formatted = formatMessageDateTime(zonedDateTime)
                        Text(
                            text = formatted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "@${message.author.login}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Аватар пользователя для своих сообщений (справа)
        if (isMyMessage) {
            if (shouldShowAvatar) {
                Spacer(modifier = Modifier.size(8.dp))
                UserAvatar(message.author.icon, message.author.firstName)
            } else {
                // Добавляем отступ вместо аватара для выравнивания
                Spacer(modifier = Modifier.size(44.dp)) // 36dp (размер аватара) + 8dp (отступ)
            }
        }
    }
}

// Функция для определения, прошло ли более 15 минут между сообщениями
private fun shouldShowTimeGap(previousTime: OffsetDateTime, currentTime: OffsetDateTime): Boolean {
    // Разница в минутах между текущим и предыдущим сообщением
    val minutesDifference = ChronoUnit.MINUTES.between(previousTime, currentTime)
    return minutesDifference >= 15
}

// Обновленный компонент для отображения аватара пользователя
@Composable
fun UserAvatar(iconUrl: String?, userName: String) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.3f),
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (iconUrl != null && iconUrl != "ICON") {
            AsyncImage(
                model = iconUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .clip(CircleShape) // делает форму круглой
            )
        } else {
            // Если URL изображения отсутствует - показываем первую букву имени
            val initial = userName.firstOrNull()?.toString() ?: "?"
            Text(
                text = initial,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatFooter(me: UserData, onClick: (task: String, metaData: String?) -> Unit) {
    var text by remember { mutableStateOf("") }
    val textFieldColors = TextFieldDefaults.textFieldColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )

    fun sendMessage() {
        if (text.isNotBlank()) {
            val message = Message(
                messageId = null,
                author = me,
                content = Content(text.trim(), null),
                dateTime = OffsetDateTime.now(),
                isReplyTo = null
            )
            onClick("sendMessage", message.toJson())
            text = ""
        }
    }

    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка для добавления вложений
        IconButton(
            onClick = { onClick("openAttachmentPicker", null) },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.paperclip),
                contentDescription = "Прикрепить файл",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        // Поле ввода текста
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            placeholder = { Text("Сообщение") },
            colors = textFieldColors,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(
                onSend = { sendMessage() }
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = false,
            maxLines = 4
        )

        // Кнопка отправки сообщения
        IconButton(
            onClick = { sendMessage() },
            modifier = Modifier.size(40.dp),
            enabled = text.isNotBlank()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.send),
                contentDescription = "Отправить",
                tint = if (text.isNotBlank())
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
            )
        }
    }
}

fun formatMessageDateTime(messageDateTime: ZonedDateTime): String {
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

//   InkTheme {
//       ChatPage(data, "n1kromant", me) { _, _ ->
//
//       }
//   }
}