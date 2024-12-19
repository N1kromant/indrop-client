package com.log.indrop.Content

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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


class Main: AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()
    private lateinit var networkManager: NetworkManager
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkManager = NetworkManager(mainViewModel)

        GlobalScope.launch {
            mainViewModel.makeFakeUserData()
            mainViewModel.makeFakeChats()
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
                Screen(mainViewModel, networkManager) { intent, metaData ->
                    when(intent) {
                        "ChooseImage" -> {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        }
                        "sendMessage" -> {
                            coroutine.launch {
                                networkViewModel.newOutputMessage(metaData!!)
                                mainViewModel.addMessage(Json.decodeFromString<Message>(metaData))
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

}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Screen(viewModel: MainViewModel,networkManager: NetworkManager, onClick: (button: String, metaData: String?) -> Unit) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isHideNavBar by viewModel.isHideNavBar.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

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
                                viewModel.login()
                                viewModel.setMyId("1")
                                navController.navigate("messages")
                                Toast.makeText(context, "Успешная Авторизация!", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                coroutineScope.launch {
                                    val loginResponse = networkManager.loginTry(username, password)
                                    if (loginResponse.data.authenticateUser.success) {
                                        viewModel.login()

                                        //TODO myID
                                        viewModel.setMyId("14881998")

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
                                val registerResponse = networkManager.registerTry(regLogin,regPassword,regName)
                                if (registerResponse.data.addUser.success)
                                {
                                    Toast.makeText(context, "Успешная регистрация!", Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    Toast.makeText(context, "Регистрация не удалась!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
                composable("news" ) { NewsPage(viewModel.posts) { button, metaData ->  onClick(button, metaData) } }

                composable(route = "messages") {
                    coroutineScope.launch {
                        viewModel.showNavBar()
                    }
                    MessagesPage(viewModel.chats.collectAsState().value) {
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
                composable("profile") { ProfilePage(
                    viewModel.posts,
                    viewModel.myUserData.collectAsState().value!!
                ) }
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
                        }
                    }
//                    viewModel.chats.value.forEach {
//
//                    }

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


