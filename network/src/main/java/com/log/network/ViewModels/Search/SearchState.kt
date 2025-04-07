package com.log.network.ViewModels.Search

import com.log.data.UserData
import com.log.network.ViewModels.BaseMVI.BaseState
import kotlinx.coroutines.flow.MutableStateFlow

data class SearchState(
    var searchField: String,
    var recentUsers: List<UserData?> = emptyList(),
    var allUsers: List<UserData?> = emptyList()
) : BaseState