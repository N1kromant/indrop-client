package com.log.indrop.ViewModels.Search

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.log.data.UserData
import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.network.ViewModels.BaseMVI.BaseViewModel
import org.koin.compose.koinInject
import java.util.function.ToLongFunction

class SearchViewModel(private val repo: SearchRepositoryImpl) : BaseViewModel<SearchIntent, SearchState, SearchEffect>(SearchState()) {


    override suspend fun handleIntent(intent: SearchIntent) {
        when(intent) {
            is SearchIntent.ChatPressedIntent -> { TODO("SearchViewModel is not сделано") }
            is SearchIntent.GoBackIntent -> emitEffect(SearchEffect.NavigateBackEffect)
            is SearchIntent.SearchFieldChangedIntent -> {
                repo.search(intent.newValue) { users ->
                    updateState { state ->
                        state.copy(allUsers = users)
                    }
                }
            }
            is SearchIntent.ToggleUserSelectionIntent -> {
                val newSelection = state.value.selectedUserIds.toMutableSet().apply {
                    if (contains(intent.userId)) remove(intent.userId)
                    else add(intent.userId)
                }
                updateState { it.copy(selectedUserIds = newSelection) }
            }
            is SearchIntent.CreateChatPressedIntent -> {
                val selected = state.value.selectedUserIds
                repo.createChat(selected.toList()){ chatId ->
                    if (chatId != null) {
                        emitEffect(SearchEffect.NavigateToChatEffect(chatId))
                    }
                    else {
                        emitEffect(SearchEffect.ErrorCreateChat)
                    }

                }

            }
        }
    }

}