package com.log.indrop.Content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.log.data.Content
import com.log.data.PostData
import com.log.data.UserData
import com.log.indrop.Auth.SubmitButton
import com.log.indrop.Auth.TOP_BAR_HEIGHT
import com.log.indrop.Auth.TopBar
import com.log.indrop.R
import com.log.indrop.ui.theme2.InkTheme
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.OffsetDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfilePage(postsData: MutableStateFlow<List<PostData>>, user: UserData){
    InkTheme {

        Column {

            LazyColumn {
                item {
                    ProfileHeader(user.firstName, user.lastName, user.login, user.icon)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        SubmitButton(submitButtonText = "Создать новый пост", fontSize = 16.sp) {

                        }
                    }
                }
                stickyHeader {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Text(text = "Мои посты:", Modifier.padding(8.dp))
                    }
                }
                items(
                    postsData.value
                ) {post ->
                    Post(post)
    //                Post(postsData.collectAsState().value[it])
                }
            }
        }

//        LazyColumn {
//            items(postsData.collectAsState().value) { it ->
//                Post(it)
//            }
//        }

//        Column(Modifier.verticalScroll(rememberScrollState())) {
//            ProfileContent(postsData)
//        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Preview
//@Composable
//fun StickyHeaderExample() {
//    val items = (0..100).toList()
//
//    LazyColumn {
//        stickyHeader {
//            Header(text = "Group 444")
//        }
//        items(items.size) { index ->
//            if (index % 10 == 0) {
//                Header(text = "Group ${index / 10}")
//            } else {
//                Text(text = "Item $index")
//            }
//        }
//    }
//}
//
//@Composable
//fun Header(text: String) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(48.dp)
//            .background(color = Color.Gray)
//            .padding(horizontal = 16.dp),
//        contentAlignment = Alignment.CenterStart
//    ) {
//        Text(text = text, color = Color.White)
//    }
//}


@Composable
fun ProfileHeader(firstName: String, lastName: String, authorId: String, iconUrl: String?) {
    InkTheme {
        Box {
            Column {
                TopBar(height = TOP_BAR_HEIGHT)

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .fillMaxWidth()
                        .height(TOP_BAR_HEIGHT.dp)
                        .clip(RoundedCornerShape(20.dp, 20.dp))
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center)
            ) {
                myUserAvatar(
                    iconUrl = iconUrl,
                    userName = firstName,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(TOP_BAR_HEIGHT.dp)
                )
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "$firstName ",
                        fontSize = 36.sp
                    )
                    Text(
                        text = lastName,
                        fontSize = 36.sp
                    )
                }
                Text(
                    text = authorId,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun myUserAvatar(iconUrl: String?, userName: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50) // подгон под аватар
            ),
        contentAlignment = Alignment.Center
    ) {
        if (iconUrl != null && iconUrl != "ICON") {
            AsyncImage(
                model = iconUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            val initial = userName.firstOrNull()?.toString() ?: "?"
            Text(
                text = initial,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ProfileContent(postsData: MutableStateFlow<List<PostData>>) {
    Column {
        SubmitButton(submitButtonText = "Создать новый пост", fontSize = 16.sp) {

        }
        Text(text = "Мои посты:", Modifier.padding(8.dp))
        postsData.collectAsState().value.forEach {
            Post(it)
        }
    }
}

@Preview
@Composable
fun ProfilePagePreview(){
    val user = UserData(
        0,
        "n1kromant",
        "Роман",
        "Николаев",
        "ICON"
    )

    val author = UserData(
        1,
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
            PostData(0, author, dateTime, content1),
            PostData(1, author, dateTime, content2),
            PostData(2, author, dateTime, content3)
        )
    ) }

    InkTheme {
        Column {
            ProfilePage(posts, user)
        }
    }
}

@Composable
fun IntersectionExample() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Первый элемент (слева)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .size(48.dp)
                    .background(Color.Blue)
            )

            // Второй элемент (справа)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .size(48.dp)
                    .background(Color.Green)
            )
        }

        // Элемент, который находится на пересечении двух других элементов
        Text(
            text = "Intersection",
            modifier = Modifier
//                .offset(0.dp, 0.dp) // Указываем сдвиг элемента
                .background(Color.Red)
                .align(Alignment.Center)
        )
    }
}