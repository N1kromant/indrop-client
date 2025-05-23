package com.log.indrop.Content

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.log.data.Message
import com.log.indrop.Auth.AuthScreen
import com.log.network.ViewModels.MainViewModel
import com.log.indrop.R
import com.log.indrop.ui.theme2.InkTheme
import com.log.network.NetworkManager
import com.log.network.ViewModels.NetworkViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.log.indrop.ViewModels.MessagesViewModel.MessagesViewModel
import com.log.indrop.navigation.NavigationHandlerImpl
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.compose.koinInject
import com.example.graphql.MessageAddedSubscription
import com.log.data.Content
import com.log.data.UserData
import com.log.indrop.data.storage.UserPreferences
import com.log.indrop.domain.services.notification.AppVisibilityTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.retryWhen
import java.time.Instant
import java.time.ZoneOffset
import kotlin.coroutines.cancellation.CancellationException
import com.log.indrop.domain.services.notification.NotificationIntegrationManager


class Main: AppCompatActivity() {
    private val mainViewModel: MainViewModel by inject()
    private val networkViewModel: NetworkViewModel by inject()

    private val networkManager: NetworkManager by inject()
    private val userPreferences: UserPreferences by inject()
    private val notificationIntManager: NotificationIntegrationManager by inject()
//    private val notificationSubManager: NotificationSubscriptionManager by inject()


    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    var subscriptionJob: Job? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                ActivityCompat.requestPermissions(
                    this, // ваша Activity
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
//        notificationSubManager.stopBackgroundSubscription()

        if (mainViewModel.isLoggedIn.value) {
            notificationIntManager.restoreNotificationsIfNeeded(this)
        }


        GlobalScope.launch {
//            mainViewModel.makeFakeUserData()
//            mainViewModel.makeFakeChats()
            mainViewModel.makeFakePosts()

        //    networkManager.connect()
        //
        //    val chats = networkManager.getAllChats(mainViewModel.myUserData.value!!.authorId!!)
        //    mainViewModel.updateChatDataList(
        //        chats
        //    )
        }

//        GlobalScope.launch { mainViewModel.currentChat.collectAsState() }

        setContent {
            val coroutine = rememberCoroutineScope()


            InkTheme {
                KoinAndroidContext {
                    Screen() { intent, metaData ->
                        when(intent) {
                            "ChooseImage" -> {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                            }
                            "sendMessage" -> {
                                coroutine.launch {
                                    val message = Json.decodeFromString<Message>(metaData!!)
                                    val sucsess = networkManager.sendMessage(
                                        chatId = mainViewModel.currentChat.value!!.chatId.toInt(),
                                        authorId = message.author.authorId!!.toInt(),
                                        text = message.content.text!!,
                                        images = null,
                                        isReplyTo = null
                                        )
                                    Log.i("info", "Отправка сообщения: $sucsess")

                                    networkViewModel.newOutputMessage(metaData) // fixme НАХУЯ НЕ ИМЕЮ ПОНЯТИЯ
                                }
                            }
                            "startSub" -> {
                                AppVisibilityTracker.isAppInForeground = true
                                // Останавливаем предыдущую подписку, если была
                                subscriptionJob?.cancel()

                                // Запускаем новую
                                subscriptionJob = lifecycleScope.launch {
                                    startMessageAddedSubscription()
                                }
                            }
                            "cancelSub" -> {
                                subscriptionJob?.cancel()
                                subscriptionJob = null
                            }
                            "LOGOUT" -> {
                                mainViewModel.clearAllData()
                                userPreferences.clearUserData()
                                notificationIntManager.deactivateNotifications()
                                subscriptionJob?.cancel()
                                subscriptionJob = null

                                mainViewModel.makeFakePosts() // fixme ПАПРИКА
                            }
                        }
                    }
                }
            }
        }

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Toast.makeText(this, "Selected media is $uri", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (mainViewModel.isLoggedIn.value){
            AppVisibilityTracker.isAppInForeground = true
            // Останавливаем предыдущую подписку, если была
            subscriptionJob?.cancel()

            // Запускаем новую
            subscriptionJob = lifecycleScope.launch {
                startMessageAddedSubscription()
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (mainViewModel.isLoggedIn.value) {
            AppVisibilityTracker.isAppInForeground = false
            subscriptionJob?.cancel()
            subscriptionJob = null
        }
    }


    private suspend fun startMessageAddedSubscription() {
        val client = networkManager.apolloClient

        try {
            val subscription = MessageAddedSubscription(userId = mainViewModel.myId.value!!)

            client.subscription(subscription)
                .toFlow()
                .retryWhen { cause, attempt ->
                    println("Подписка завершилась: $cause, попытка $attempt")
                    true // пробуем заново
                }
                .collect { response ->
                    if (response.hasErrors()) {
                        println("Ошибка в подписке: ${response.errors}")
                    } else if (response.data != null) {
                        val message = response.data!!.messageAdded
                        println(response.data!!.messageAdded)
                        println(response.data!!.messageAdded.content.text.toString())
//                        val success2 = mainViewModel.addMessageCurrentChat(
//                            Message(message.messageId.toLong(),
//                                author = UserData(message.author.authorId.toLong(), message.author.login, message.author.firstName, message.author.lastName, message.author.icon),
//                                dateTime = Instant.ofEpochMilli(message.dateTime.toLong()).atOffset(
//                                    ZoneOffset.UTC) ,
//                                content = Content(message.content.text, message.content.images),
//                                isReplyTo = message.isReplyTo?.toLong()
//                                ))

                        val success = mainViewModel.addMessage(
                            response.data!!.messageAdded.chatId.toLong(),
                            Message(message.messageId.toLong(),
                                author = UserData(message.author.authorId.toLong(), message.author.login, message.author.firstName, message.author.lastName, message.author.icon),
                                dateTime = Instant.ofEpochMilli(message.dateTime.toLong()).atOffset(
                                    ZoneOffset.UTC) ,
                                content = Content(message.content.text, message.content.images),
                                isReplyTo = message.isReplyTo?.toLong()
                            )
                        )

                    }
                }
        } catch (e: CancellationException) {
            println("Предыдущая подписка отменена из-за запуска новой")
        } catch (e: Exception) {
            println("Ошибка в подписке: ${e.message}")
            e.printStackTrace()
        }
    }
}



@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Screen(viewModel: MainViewModel = koinInject(),
           userPreferences: UserPreferences = koinInject<UserPreferences>(),
           networkManager: NetworkManager = koinInject(),
           messagesViewModel: MessagesViewModel = koinInject(),
           navigationHandler: NavigationHandlerImpl = koinInject(),
           notificationManager: NotificationIntegrationManager = koinInject(),
           onClick: (button: String, metaData: String?) -> Unit) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isHideNavBar by viewModel.isHideNavBar.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current


    val context = LocalContext.current
    navigationHandler.setNavController(navController)

//    val messagesViewModel by messagesViewModel.state.collectAsState()
    val messagesState by messagesViewModel.state.collectAsState()

    // Для эффектов (событий) используем отдельный LaunchedEffect


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {
        Column {
            NavHost(
                navController = navController,
                startDestination = if (!isLoggedIn) "auth" else "messages",
                modifier = Modifier
                    .weight(weight = 0.9f, fill = true)
            ) {
                composable("auth") {
                    AuthScreen(
                        isLoggedIn = { username, password ->

                            //для тестов фаст вход //FIXME убрать
                            if(username == "" && password == "") {
                                println("НАХУЙ ИДИ!!!!!!")
                                viewModel.login()
                                viewModel.setMyId("1")
                                viewModel.makeFakeUserData()
                                navController.navigate("messages")
                                Toast.makeText(context, "Успешная Авторизация!", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                coroutineScope.launch {
                                    val loginResponse = networkManager.loginUser(login = username, password = password)
                                    if (loginResponse.success) {
                                        viewModel.login()
                                        viewModel.setMyId(loginResponse.userId.toString())
                                        viewModel.makeTrueUserData(loginResponse.UserData!!)

                                        // После этого активируйте уведомления, передав MainViewModel и LifecycleOwner
                                        notificationManager.setupNotificationsWithViewModel(viewModel, lifecycleOwner)
                                        onClick("startSub", null)


                                        navController.navigate("messages")
                                        Toast.makeText(context, "Успешная Авторизация!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        navController.navigate("auth")
                                        Toast.makeText(context, "Авторизация не удалась!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        },
                        isRegisterIn = { regLogin,
                                         regPassword,
                                         regName,
                                          ->
                            coroutineScope.launch {
                                val registerResponse = networkManager.registerUser(login = regLogin,password = regPassword, firstName = regName)
                                if (registerResponse)
                                {
                                    Toast.makeText(context, "Успешная регистрация!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("auth")
                                }
                                else
                                {
                                    Toast.makeText(context, "Регистрация не удалась!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("auth")
                                }
                            }
                        }
                    )
                }
                composable("news") { NewsPage(navController = navController) }

                composable(route = "messages") {
                    coroutineScope.launch {
                        viewModel.showNavBar()
                        viewModel.updateChatDataList( networkManager.getChats(viewModel.myId.value!!.toInt()) ) //TODO: это колхоз потом испривитиь
                    }
                    MessagesPage(chats = viewModel.chats.collectAsState().value, navController = navController) {
                        try {
                        coroutineScope.launch {
                            viewModel.openChat(it)
                        }
                        } catch (e: Exception) {
                            Log.e("CoroutineError", "Error occurred: ${e.message}")
                        }
                        navController.navigate("chat")
                    }
                }
                composable("profile") {
                    val user = viewModel.myUserData.collectAsState().value

                    user?.let { safeUser ->
                        ProfilePage(
                            postsData = viewModel.posts,
                            user = safeUser
                        ) { task, metaData ->
                            when (task) {
                                "logout" -> {
                                    navController.navigate("auth") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                    onClick("LOGOUT", metaData)
                                }
                            }
                        }
                    } ?: LaunchedEffect(Unit) {
                        navController.navigate("auth") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                }
                composable(
                    route = "chat",
                ) {
                    coroutineScope.launch {
                        viewModel.hideNavBar()
                    }
                    ChatPage(
                        data = viewModel.currentChat.collectAsState().value!!,
                        myId = viewModel.myId.collectAsState().value!!,
                        me = viewModel.myUserData.collectAsState().value!!
                    ) { task, metaData ->
                        when(task) {
                            "goBack" -> navController.navigate("messages") {
                                popUpTo("messages")
                            }
                            "sendMessage" -> onClick("sendMessage", metaData)
                            "startSubscription" -> {}
                            "cancelSubscription" -> {}
                        }
                    }
//                    viewModel.chats.value.forEach {
//
//                    }

                }
                composable("search") {
                    SearchPage(navController = navController)

                }
            }
//            Spacer(modifier = Modifier.weight(weight = 0.1f, fill = true))

            if (isLoggedIn && !isHideNavBar) {
                NavBar(Modifier) {
                    if (navController.currentDestination?.route != it) {
//                        if (it == "messages") navController.navigate(it) {popUpTo("messages")}
                        when(it) {
                            "news" -> navController.navigate(it) { popUpTo("messages") }
                            "profile" -> navController.navigate(it) { popUpTo("messages") }
                            "messages" -> navController.navigate(it) { popUpTo("auth") }
                            "chat" -> navController.navigate(it) { popUpTo("messages") }
                            else -> navController.navigate(it)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavBar(modifier: Modifier, onNavigate: (destination: String) -> Unit)  {
    val theme = MaterialTheme.colorScheme
    val buttonHeight = 64.dp
    val buttonColors = ButtonColors(
        contentColor = theme.onPrimary,
        containerColor = Color.Transparent,
        disabledContentColor = theme.onError,
        disabledContainerColor = Color.Transparent
    )

    Row (
        modifier = modifier
            .background(theme.primaryContainer)
    ) {
        Button(
            onClick = { onNavigate("news") },
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight)
                .background(theme.primaryContainer),
            colors = buttonColors

        ) {
            Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Новости")
        }
        Button(
            onClick = { onNavigate("messages") },
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight)
                .background(theme.primaryContainer),
            colors = buttonColors

        ) {
            Icon(painter = painterResource(id = R.drawable.messages), contentDescription = "Сообщения")
        }
        Button(
            onClick = { onNavigate("profile") },
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight)
                .background(theme.primaryContainer),
            colors = buttonColors
        ) {
            Icon(painter = painterResource(id = R.drawable.profile), contentDescription = "Профиль")
        }
    }
}

@Preview
@Composable
fun NavBarPreview() {
    InkTheme {
        NavBar(modifier = Modifier) {

        }
    }
}


