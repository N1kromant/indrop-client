package com.log.indrop.Content

import android.os.Bundle
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class Main: AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()
    private lateinit var networkManager: NetworkManager
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkManager = NetworkManager(mainViewModel)

        mainViewModel.makeFakeUserData()
        mainViewModel.makeFakeChats()
        mainViewModel.makeFakePosts()

        GlobalScope.launch { networkManager.connect() }
//        GlobalScope.launch { mainViewModel.currentChat.collectAsState() }

        setContent {
            InkTheme {
                Screen(mainViewModel) { intent, metaData ->
                    when(intent) {
                        "ChooseImage" -> {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        }
                        "sendMessage" -> {
                            networkViewModel.newOutputMessage(metaData!!)
                            mainViewModel.addMessage(Json.decodeFromString<Message>(metaData))
                        }
                    }
                }
                //                if (viewModel.isLoggedIn.collectAsState().value) Screen()
                //                else AuthScreen() {viewModel.login()}
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

@Composable
fun Screen(viewModel: MainViewModel, onClick: (button: String, metaData: String?) -> Unit) {
    val navController = rememberNavController()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isHideNavBar by viewModel.isHideNavBar.collectAsState()

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
                composable("auth") { AuthScreen() {
                    viewModel.setMyId("n1kromant")
                    viewModel.login()
                    navController.navigate("messages")
                } }
                composable("news" ) { NewsPage(viewModel.posts) { button, metaData ->  onClick(button, metaData) } }

                composable(route = "messages") {
                    viewModel.showNavBar()
                    MessagesPage(viewModel.chats.collectAsState().value) {
                        viewModel.openChat(it)
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
                    viewModel.hideNavBar()
                    ChatPage(
                        data = viewModel.currentChat,
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