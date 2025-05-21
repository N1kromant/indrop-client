package com.log.indrop.ViewModels.News

import com.log.data.Comment
import com.log.data.Likeable
import com.log.data.PostData
import com.log.network.ViewModels.BaseMVI.BaseIntent

sealed class NewsIntent: BaseIntent {
    data class ToggleLikeIntent(val entity: Likeable) : NewsIntent()

    data class CreatePostIntent(val post: PostData) : NewsIntent()
    data class EditPostIntent(val post: PostData) : NewsIntent()
    data class DeletePostIntent(val post: PostData) : NewsIntent()

    data class CreateCommentIntent(val comment: Comment) : NewsIntent()
    data class DeleteCommentIntent(val comment: Comment) : NewsIntent()
    data class EditCommentIntent(val comment: Comment) : NewsIntent()

    data class RouteProfileIntent(val user: Long) : NewsIntent()
}
