package com.log.data

data class SearchState(
    var SearchTextField: String,
var RecentUsers: List<UserData?>,
//    var NewUsers: List<UserData?>
)

sealed interface SearchIntent {
    data class QueryChanged(val query: String)
    data object SearchClicked: SearchIntent
}