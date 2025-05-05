package com.log.indrop.ViewModels.Search

import com.log.data.UserData
import com.log.network.ViewModels.BaseMVI.BaseState
import kotlinx.coroutines.flow.MutableStateFlow

data class SearchState(
    var searchField: String = "",
    var recentUsers: List<UserData?> = emptyList(), //Можно брать из последних 3 чатов
    var allUsers: List<UserData?> = emptyList()
) : BaseState