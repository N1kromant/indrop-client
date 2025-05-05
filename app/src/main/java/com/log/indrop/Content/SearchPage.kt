package com.log.indrop.Content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchPage(viewModel: SearchViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Заголовок
        stickyHeader {
            Text(text = stringResource(id = R.string.all_users))
        }

        // Список пользователей
        itemsIndexed(
            items = state.allUsers,
            key = { index, user -> user?.authorId ?: index }
        ) { _, user ->
            user?.let { userdata ->
                UserListItem(
                    name = "${userdata.firstName} ${userdata.lastName}",
                    login = userdata.login,
                ) {
                    viewModel.processIntent(SearchIntent.ChatPressedIntent(userdata.authorId ?: 0))
                }
            }
        }
    }
//    viewModel.processIntent(SearchIntent.GoBackIntent)
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
            }
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