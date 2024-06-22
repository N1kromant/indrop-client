package com.log.indrop.Content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.log.data.Content
import com.log.data.PostData
import com.log.data.UserData
import com.log.indrop.R
import com.log.indrop.ui.theme2.InkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

val footerFontSize = 16.sp

//@Preview
//@Composable
//fun ViewsCount() {
//    val theme = MaterialTheme.colorScheme
//
//
//    Row (
//        modifier = Modifier
//            .requiredWidth(IntrinsicSize.Max)
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.eye_icon),
//            contentDescription = "views"
//        )
//        Text(
//            text = "4444",
//            Modifier
//                .align(Alignment.CenterVertically),
//            color = theme.onPrimary,
//        )
//    }
//}

@Composable
fun PostContent(
    content: Content
) {
    Row (
        Modifier.fillMaxWidth()
    ) {
        if (content.text != null) {

            Text(
                text = content.text!!,
//                style = TextStyle(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun Post(
        postData: PostData
             //author: UserData, dateTime: OffsetDateTime, content: Content
     ) {

//    val postData = remember { mutableStateOf(PostData) }

    Row(
        Modifier
            .fillMaxWidth(),
    ) {
        Column {
            Row (
                Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    )
            ){
                Spacer(modifier = Modifier
                    .size(12.dp)
                )
                Column (
                    Modifier.weight(0.9f)
                ){
                    Spacer(
                        modifier = Modifier
                            .height(6.dp)
                    )
                    PostHeader(
                        postData
                    )

                    PostContent(
                        content = postData.content
                    )

                    PostFooter(
                        likes = 44,
                        comments = 4,
                        views = 44

                    )
                    Spacer(
                        modifier = Modifier
                            .height(6.dp)
                    )
                }
                Spacer(modifier = Modifier
                    .size(12.dp)
                )
            }
            Spacer(modifier = Modifier
                .size(2.dp)
//                .background(
//                    MaterialTheme.colorScheme.tertiary
//                )
            )
        }
    }
}

fun OffsetDateTime.formatOffsetDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'в' HH:mm")
    return format(formatter)
}

//fun main() {
//    val offsetDateTime = OffsetDateTime.now()
//    val formattedString = formatOffsetDateTime(offsetDateTime)
//    println("Форматированная дата и время: $formattedString")
//}

@Preview
@Composable
fun PostHeaderPreview() {
    val author = UserData(
        1,
        "puffer",
        "Sofia",
        "Tyuleneva",
        "uri"
    )
    val dateTime: OffsetDateTime = OffsetDateTime.now()

    val content = Content(
        text = "Hello, N",
        images = null
    )

    PostHeader(
        PostData(
            1,
            author = author,
            dateTime = dateTime,
            content = content,
            comments = null
        )
    )
}

@Composable
fun PostHeader(
    postData: PostData
) {
    Row {
        Icon(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Аватар",
            tint = MaterialTheme.colorScheme.onPrimary,
        )
        Column {
            Row {
                Text(
                    text = postData.author.firstName,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = postData.author.lastName,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Row {
                Text(
                    text = postData.dateTime.formatOffsetDateTime(),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )
        }
    }
}

@Composable
fun PostFooter(
        likes: Int,
        comments: Int,
        views: Int
    ) {
    val isLiked = remember { mutableStateOf(false) }

    Column {
        Spacer(
            modifier = Modifier
                .height(12.dp)
        )
        Row(
            Modifier.fillMaxWidth()
        ){
            LikesCount(
                isLiked,
                likes
            ) {}
            CommentsCount(
                comments
            )
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            ViewsCount(
                views
            )
        }
    }
}

@Preview
@Composable
fun PostFooterPreview() {
    PostFooter(
        44,
        4,
        4
    )
}


@Composable
fun NewsPage(postsData: MutableStateFlow<List<PostData>>, onClick: (button: String, metaData: String?) -> Unit) {
    LazyColumn {
        item {
            Text(
                text = "News Page",
                fontSize = 48.sp,
            )
        }
        items(postsData.value) {
            Post(it)
        }
//        item {
//            SubmitButton(
//                submitButtonText = "Выбрать фото",
//                fontSize = 28.sp
//            ) {
//                onClick("ChooseImage")
//            }
//        }
    }

}

@Preview
@Composable
fun NewsPagePreview() {

    val author = UserData(
        0,
        "Puffer",
        "Sofia",
        "Tyuleneva",
        "uri"
    )
    val dateTime: OffsetDateTime = OffsetDateTime.now()

    val content1 = Content(
        text = "Поиск потерянного ключа\n" +
                "Соня, маленькая девочка с густыми волосами и веселыми глазами, потеряла ключи от дома. Она обыскала каждый уголок своей комнаты, но ключи так и не нашла. Отчаявшись, она решила вспомнить, где они могли оказаться. Внезапно ей вспомнилось, что она играла во дворе у дерева. Она побежала туда и нашла ключи под кустом цветов.",
        images = null
    )

    val content2 = Content(
        text = "Приключение в лесу\n" +
                "Соня отправилась на прогулку в лес вместе со своим верным другом, собакой по имени Рекс. Они встретили множество животных: белку, зайца и даже оленя. Вдруг они наткнулись на огромный дуб, ветви которого сливались с небесами. Они взобрались на верхушку и смогли увидеть великолепный закат.\n",
        images = null
    )

    val content3 = Content(
        text = "история о Чорном Диме:\n" +
                "\n" +
                "Черный Дима был самым загадочным обитателем небольшого городка, окруженного пышными лесами. Его настоящее имя никто не знал, и все окружающие просто называли его Черным Димой из-за его густых черных волос, которые всегда были небрежно растрепаны. Он жил в старом доме на окраине города, вдали от суеты и шума.\n" +
                "\n" +
                "У Черного Димы была особая способность — он мог говорить с животными. Каждый день он проводил в лесу, общаясь с разными созданиями, от маленьких белок и птиц до огромных медведей и волков. Они слушали его и помогали ему в трудные моменты.\n" +
                "\n" +
                "Однажды, когда в городе началась странная серия происшествий, Черный Дима решил взять дело в свои руки. С помощью своих верных друзей-зверей он раскрыл тайну и помог городу вернуть мир и спокойствие.\n" +
                "\n" +
                "Хотя Черный Дима и оставался загадочной фигурой для многих, все жители городка знали, что в случае опасности они могут полагаться на его мудрость и смелость. Он стал легендой, и его истории рассказывались детям по ночам перед сном.",
        images = null
    )

    val posts = remember { MutableStateFlow(
        listOf(
            PostData(0, author, dateTime, content1, null),
            PostData(1, author, dateTime, content2, null),
            PostData(2, author, dateTime, content3, null)
        )
    ) }

    InkTheme {
        Column {
            NewsPage(posts) { button, metaData ->  

            }
            NavBar(modifier = Modifier.weight(0.2f)) {

            }
        }
    }
}
@Composable
fun ViewsCount(
    count: Int?
) {
    val theme = MaterialTheme.colorScheme

    Row (
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(id = R.drawable.eye_icon), contentDescription = "views")
        Text(
            text = "$count",
            Modifier
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = footerFontSize),
            color = theme.onPrimary,
        )
    }
}

@Preview(showBackground = false)
@Composable
fun ViewsCountPreview() {
    InkTheme {
        ViewsCount(4444)
    }
}

@Composable
fun LikesCount(
    isPressed: MutableState<Boolean>,
    count: Int?,
    onLike: () -> Unit
) {
    val theme = MaterialTheme.colorScheme

    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .wrapContentWidth()
    ) {
        if (isPressed.value)
            Icon(
                tint = theme.tertiary,
                painter = painterResource(id = R.drawable.heart_filled),
                contentDescription = "likes",
                modifier = Modifier.clickable {
                    isPressed.value = !isPressed.value
                    onLike()
                }
            )
        else
            Icon(
                tint = theme.tertiary,
                painter = painterResource(id = R.drawable.heart_outlined),
                contentDescription = "likes",
                modifier = Modifier.clickable {
                    isPressed.value = !isPressed.value
                    onLike()
                }
            )
        Text(
            text = "${if (isPressed.value) {count!! + 1} else count!!}",
            Modifier
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = footerFontSize),
            color = theme.onPrimary,
        )
    }
}

@Preview(showBackground = false)
@Composable
fun LikesCountPreview() {
    LikesCount(remember { mutableStateOf(false) } , 4444,) {}
}

@Composable
fun CommentsCount(
    count: Int?
) {
    val theme = MaterialTheme.colorScheme

    Row (
        modifier = Modifier
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painter = painterResource(id = R.drawable.message), contentDescription = "comments")

        Text(
            text = "$count",
            Modifier
                .align(Alignment.CenterVertically),
            style = TextStyle(fontSize = footerFontSize),
            color = theme.onPrimary,
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CommentsCountPreview() {
    CommentsCount(4444)
}