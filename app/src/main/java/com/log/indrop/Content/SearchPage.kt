package com.log.indrop.Content

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.log.indrop.R
import com.log.indrop.ui.theme2.InkTheme
import com.log.indrop.ViewModels.Search.SearchIntent
import com.log.indrop.ViewModels.Search.SearchViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.indrop.api.SearchApiTestImpl

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.log.indrop.ViewModels.MessagesViewModel.MessagesEffect
import com.log.indrop.ViewModels.Search.SearchEffect
import com.log.network.ViewModels.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.get
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(viewModel: SearchViewModel = koinInject<SearchViewModel>(), mainViewModel: MainViewModel = koinInject<MainViewModel>(), navController: NavController) {
    val state by viewModel.state.collectAsState()
    var query by remember { mutableStateOf("") }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    var showChatNameInput by remember { mutableStateOf(false) }
    var chatName by remember { mutableStateOf("") }
    val myId by mainViewModel.myId.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SearchEffect.NavigateToChatEffect -> {
                    Toast.makeText(context, "Чат создан!", Toast.LENGTH_SHORT).show()
                    navController.navigate("messages")
                }
                is SearchEffect.ErrorCreateChat -> {
                    Toast.makeText(context, "Чат не создан!", Toast.LENGTH_SHORT).show()
                }
                is SearchEffect.NavigateBackEffect -> {}
            }
        }
    }

    mainViewModel.hideNavBar()
    viewModel.processIntent(SearchIntent.SearchFieldChangedIntent(query))
    Column(modifier = Modifier.fillMaxSize()) {

        // 🔍 Поле поиска и кнопка
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            IconButton(onClick = {navController.navigate("messages")}) {
                Icon(painter = painterResource(id = R.drawable.go_back), contentDescription = "goBack", tint = MaterialTheme.colorScheme.onPrimary)
            }
            TextField(
                value = query,
                singleLine = true, // чтобы не было переноса строки
                onValueChange = {
                    query = it
                    viewModel.processIntent(SearchIntent.SearchFieldChangedIntent(query))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // показываем кнопку "Done" на клавиатуре
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // скрываем клавиатуру
                    }
                ),
                modifier = Modifier.weight(1f),
                placeholder = { Text("Введите логин или имя") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

        }

        // 📜 Список пользователей
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            stickyHeader {
                Text(
                    text = stringResource(id = R.string.all_users),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                )
            }

            itemsIndexed(
                items = state.allUsers,
                key = { index, user -> user?.authorId ?: index }
            ) { _, user ->
                user?.let { userdata ->
                    if (user.authorId == myId?.toLong()) return@itemsIndexed
                    UserListItemWithCheckbox(
                        name = "${userdata.firstName} ${userdata.lastName}",
                        login = userdata.login,
                        checked = state.selectedUserIds.contains(userdata.authorId),
                        onCheckedChange = {
                            viewModel.processIntent(
                                SearchIntent.ToggleUserSelectionIntent(userdata.authorId ?: 0)
                            )
                        }
                    )
                }
            }
        }

        // ➕ Кнопка "Создать чат"


        AnimatedVisibility(state.selectedUserIds.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    singleLine = true,
                    value = chatName,
                    onValueChange = { chatName = it; state.newChatTitle = chatName },
                    label = { Text("Название чата") },
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        showChatNameInput = false
                        chatName = ""
                    }) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        showChatNameInput = false
                        viewModel.processIntent(SearchIntent.CreateChatPressedIntent(chatName, null, mainViewModel.myId.value!!.toLong())) // TODO: это колхоз потом испривитиь
                        chatName = ""
                    }) {
                        Text("Создать")
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItemWithCheckbox(
    name: String,
    login: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 12.dp) // Добавляем вертикальные отступы
        // Или можно задать минимальную высоту
        // .heightIn(min = 72.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            // Добавляем вертикальные отступы внутри колонки
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(text = name)
            Spacer(modifier = Modifier.height(4.dp)) // Добавляем отступ между текстами
            Text(text = "@$login", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

//@Preview
//@Composable
//fun SearchPagePreview() {
//    val viewModel = SearchViewModel(
//        SearchRepositoryImpl(
//            SearchApiTestImpl()
//        )
//    )
//    SearchPage(
//        viewModel
//    )
//}


@Composable
fun UserListItem(name: String, login: String, onClick: (event: String) -> Unit) {
    Column (
        Modifier
            .clickable { onClick("") }

    ) {
        ListItem(
            headlineContent = { Text(text = name) },
            leadingContent = {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Avatar",
                    Modifier
                )
            },
            supportingContent = {
                Text(login)
            },
        )
    }
}

@Preview
@Composable
fun UserListItemPreview() {
    InkTheme {
        UserListItem("Дима Чорни", "averdroz", {})
    }
}