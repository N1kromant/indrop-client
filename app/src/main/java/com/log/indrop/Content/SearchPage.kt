package com.log.indrop.Content

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.log.indrop.R
import com.log.indrop.ui.theme2.InkTheme

@Composable
fun SearchPage() {

}

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