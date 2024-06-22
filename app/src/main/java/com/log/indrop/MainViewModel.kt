package com.log.indrop

import androidx.lifecycle.ViewModel
import com.log.data.ChatData
import com.log.data.Content
import com.log.data.Message
import com.log.data.PostData
import com.log.data.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.OffsetDateTime

class MainViewModel: ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    var isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isHideNavBar = MutableStateFlow(false)
    var isHideNavBar = _isHideNavBar.asStateFlow()

    private val _currentChat = MutableStateFlow<ChatData?>(null)
    var currentChat = _currentChat.asStateFlow()

    private val _myId = MutableStateFlow<String?>(null)
    var myId = _myId.asStateFlow()

    private val _myUserData = MutableStateFlow<UserData?>(null)
    var myUserData = _myUserData.asStateFlow()

    private val _chats: MutableStateFlow<List<ChatData>> = MutableStateFlow(emptyList())
    val chats: MutableStateFlow<List<ChatData>>
        get() = _chats

    private val _posts: MutableStateFlow<List<PostData>> = MutableStateFlow(emptyList())
    val posts: MutableStateFlow<List<PostData>>
        get() = _posts

    fun openChat(chatData: ChatData) {
        _currentChat.value = chatData
    }

    fun setMyId(id: String) {
        _myId.value = id
    }

    fun myUserData(): UserData {
        lateinit var me: UserData

        me = TODO("Получение с сервера")

        return me
    }

    fun loadPosts(postsData: MutableList<PostData> ) {
        _posts.value = postsData
    }

    fun hideNavBar() {
        _isHideNavBar.value = true
    }

    fun showNavBar() {
        _isHideNavBar.value = false

    }

    fun login() {
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // Метод для обновления списка чатов
    fun updateChatDataList(newList: List<ChatData>) {
        _chats.value = newList
    }

    fun makeFakeUserData() {
        val me = UserData(
            0,
            "n1kromant",
            "Роман",
            "Николаев",
            "ICON"
        )

        _myUserData.value = me
    }

    fun makeFakePosts() {
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



        val posts = mutableListOf(
            PostData(0, author, dateTime, content1, null),
            PostData(1, author, dateTime, content2, null),
            PostData(2, author, dateTime, content3, null)
        )

        loadPosts(posts)
    }

    fun makeFakeChats() {

        val author1 = UserData(
            1,
            "puffer",
            "Соняша",
            "",
            "ICON"
        )
        val author2 = UserData(
            2,
            "krairox",
            "Матвей",
            "Который Н",
            "ICON"
        )
        val author3 = UserData(
            3,
            "averdroz",
            "Хаха",
            "Чорни",
            "ICON"
        )
        val author4 = UserData(
            4,
            "menger",
            "Максим",
            "Решето",
            "ICON"
        )
        val author0 = UserData(
            0,
            "n1kromant",
            "Роман",
            "Ник",
            "ICON"
        )

        val content = Content(
            "Привет, лучший друг!",
            null
        )
        val content2 = Content(
            "Привет, лучший подруг!",
            null
        )

        val authors = listOf(author0, author1, author2, author3, author4)

        for (i in (1..4) ) {
            val messages: MutableList<Message> = mutableListOf()


            messages += Message(
                3,
                author = authors[i],
                dateTime = OffsetDateTime.now(),
                content = content,
                isReplyTo = null
            )
            messages += Message(
                4,
                author = authors[0],
                dateTime = OffsetDateTime.now(),
                content = content2,
                isReplyTo = null
            )
            messages += Message(
                5,
                author = authors[i],
                dateTime = OffsetDateTime.now(),
                content = content,
                isReplyTo = null
            )
            messages += Message(
                6,
                author = authors[0],
                dateTime = OffsetDateTime.now(),
                content = content2,
                isReplyTo = null
            )

            val data = ChatData(
                chatId = i.toLong(),
                members = mutableListOf(0,i+1),
                title = authors[i].firstName + " " +authors[i].lastName,
                avatar = null,
                messages = messages
            )
            _chats.value += data
        }

    }
}