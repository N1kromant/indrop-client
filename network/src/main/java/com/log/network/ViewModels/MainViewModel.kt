package com.log.network.ViewModels

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
        get() = _currentChat

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


    fun clearAllData(){
        _isLoggedIn.value = false
        _isHideNavBar.value = false
        _currentChat.value = null
        _myId.value = null
        _myUserData.value = null
        _chats.value = emptyList()
        _posts.value = emptyList()
    }

    fun openChat(chatData: ChatData): Boolean {
        val chat = _chats.value.find { it.chatId == chatData.chatId }
        if (chat != null) {
            _currentChat.value = chat  // Устанавливаем ссылку на объект из списка

            return true
        }
        return false
    }

    fun setMyId(id: String) {
        _myId.value = id
    }

    /**
     * Сортирует список чатов по времени последнего сообщения
     * (свежие чаты с последними сообщениями будут в начале списка)
     */
    fun sortChatsByLatestMessage() {
        val currentChats = _chats.value
        if (currentChats.isEmpty()) return

        // Сортируем чаты по времени последнего сообщения (в порядке убывания)
        val sortedChats = currentChats.sortedByDescending { chat ->
            // Если в чате нет сообщений, ставим его в конец списка
            if (chat.messages.isEmpty()) {
                OffsetDateTime.MIN
            } else {
                chat.getLastMessage()!!.dateTime
            }
        }

        // Обновляем список чатов отсортированным
        _chats.value = sortedChats
    }

    /**
     * Добавляет сообщение в текущий чат
     *
     * @param message Сообщение для добавления
     */
    fun addMessageCurrentChat(message: Message) {

        _currentChat.value = _currentChat.value!!.copy(messages = _currentChat.value!!.messages + message)

        sortChatsByLatestMessage()
    }

    /**
     * Добавляет сообщение в чат с указанным идентификатором
     *
     * @param chatId Идентификатор чата, в который нужно добавить сообщение
     * @param message Сообщение для добавления
     * @return true если сообщение было добавлено, false если чат не найден
     */
    fun addMessage(chatId: Long, message: Message): Boolean {
        // Получаем текущий список чатов
        val currentChats = _chats.value

        // Находим индекс чата с указанным chatId
        val chatIndex = currentChats.indexOfFirst { it.chatId == chatId }

        // Если чат не найден, возвращаем false
        if (chatIndex == -1) {
            return false
        }

        _chats.value = currentChats.map { chat ->
            if (chat.chatId == chatId) {
                chat.copy(messages = chat.messages + message)
            } else {
                chat
            }
        }
        _currentChat.value?.let{addMessageCurrentChat(message)}
        sortChatsByLatestMessage()

        return true
    }

    /**
     * Обновляет чат в списке чатов
     *
     * @param updatedChat Обновленные данные чата
     * @return true если чат найден и обновлен, false в противном случае
     */
    fun updateChat(updatedChat: ChatData): Boolean {
        val chatIndex = _chats.value.indexOfFirst { it.chatId == updatedChat.chatId }
        if (chatIndex == -1) return false

        val newChats = _chats.value.toMutableList()
        newChats[chatIndex] = updatedChat
        _chats.value = newChats

        // Если обновляемый чат - текущий, то нужно обновить и ссылку на него
        if (_currentChat.value?.chatId == updatedChat.chatId) {
            _currentChat.value = updatedChat
        }

        sortChatsByLatestMessage()
        return true
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
        _myId.value = null
    }

    // Метод для обновления списка чатов
    fun updateChatDataList(newList: List<ChatData>) {
        _chats.value = newList
    }


    fun clearChatData() {
        _chats.value = emptyList()
    }


    fun makeTrueUserData(UserData: UserData) {
        val me = UserData(
            UserData.authorId,
            UserData.login,
            UserData.firstName,
            UserData.lastName,
            UserData.icon
        )

        _myUserData.value = me
    }

    fun makeFakeUserData() {
        val me = UserData(
            1,
            "n1kromant",
            "Роман",
            "Николаев",
            "ICON"
        )

        _myUserData.value = me
    }

    fun makeFakePosts() {
        val author = UserData(
            3,
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
            PostData(0, author, dateTime, content1),
            PostData(1, author, dateTime, content2),
            PostData(2, author, dateTime, content3)
        )

        loadPosts(posts)
    }

    fun makeFakeChats() {

        val author1 = UserData(
            3,
            "puffer",
            "Соняша",
            "",
            "ICON"
        )
        val author2 = UserData(
            4,
            "krairox",
            "Матвей",
            "Который Н",
            "ICON"
        )
        val author3 = UserData(
            5,
            "averdroz",
            "Дима",
            "Чорни",
            "ICON"
        )
        val author4 = UserData(
            6,
            "menger",
            "Максим",
            "Решето",
            "ICON"
        )
        val author0 = UserData(
            1,
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
            messages += Message(
                7,
                author = authors[i],
                dateTime = OffsetDateTime.now(),
                content = content,
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