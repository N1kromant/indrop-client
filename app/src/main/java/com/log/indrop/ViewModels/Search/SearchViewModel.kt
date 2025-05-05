package com.log.indrop.ViewModels.Search

import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.network.ViewModels.BaseMVI.BaseViewModel
import org.koin.compose.koinInject

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
        }
    }

}