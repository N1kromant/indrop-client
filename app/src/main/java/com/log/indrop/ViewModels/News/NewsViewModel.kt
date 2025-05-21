package com.log.indrop.ViewModels.News

import com.log.data.Comment
import com.log.indrop.ViewModels.News.NewsIntent.CreatePostIntent
import com.log.indrop.domain.usecases.CreateCommentCase
import com.log.indrop.domain.usecases.CreatePostCase
import com.log.indrop.domain.usecases.DeleteCommentCase
import com.log.indrop.domain.usecases.DeletePostCase
import com.log.indrop.domain.usecases.EditCommentCase
import com.log.indrop.domain.usecases.EditPostCase
import com.log.indrop.domain.usecases.RouteProfileCase
import com.log.indrop.domain.usecases.ToggleLikeCase
import com.log.network.ViewModels.BaseMVI.BaseViewModel

class NewsViewModel: BaseViewModel<NewsIntent, NewsState, NewsEffect> (NewsState()) {
    override suspend fun handleIntent(intent: NewsIntent) {
        when (intent) {
            is NewsIntent.ToggleLikeIntent -> handleToggleLikeIntent(intent)

            is CreatePostIntent -> handleCreatePostIntent(intent)
            is NewsIntent.DeletePostIntent -> handleDeletePostIntent(intent)
            is NewsIntent.EditPostIntent -> handleEditPostIntent(intent)

            is NewsIntent.CreateCommentIntent -> handleCreateCommentIntent(intent)
            is NewsIntent.DeleteCommentIntent -> handleDeleteCommentIntent(intent)
            is NewsIntent.EditCommentIntent -> handleEditCommentIntent(intent)

            is NewsIntent.RouteProfileIntent -> handleRouteProfileIntent(intent)
        }
    }

    private fun handleToggleLikeIntent(intent: NewsIntent.ToggleLikeIntent) {
        when (intent.entity) {
            is Comment -> {
                val updatedPosts = state.value.posts.map { post ->
                    // Если это пост, к которому относится комментарий
                    if (post.postId == intent.entity.postId) {
                        val updatedComments = post.comments.map { comment ->
                            // Если это комментарий, для которого мы делаем toggle like
                            if (comment.id == intent.entity.id) {
                                // Возвращаем обновленный комментарий
                                comment.copy(
                                    isLiked = intent.entity.isLiked,
                                    likesCount = intent.entity.likesCount + 1
                                )
                            } else {
                                // Остальные комментарии оставляем без изменений
                                comment
                            }
                        }

                        post.copy(comments = updatedComments)
                    } else {
                        // Остальные посты оставляем без изменений
                        post
                    }
                }

                updateState {
                    it.copy(posts = updatedPosts)
                }
            }
        }
        ToggleLikeCase() // Можно поднять выше. Там есть разделение на комменты и посты
    }

    private fun handleCreatePostIntent(intent: CreatePostIntent) {

        updateState {
            it.copy(posts = listOf(intent.post) + it.posts)
        }

        CreatePostCase()
    }

    private fun handleDeletePostIntent(intent: NewsIntent.DeletePostIntent) {
        updateState {
            it.copy(posts = it.posts - intent.post)
        }

        DeletePostCase()
    }

    private fun handleEditPostIntent(intent: NewsIntent.EditPostIntent) {
        updateState {
            it.copy(posts = it.posts.map { post ->
                if (post.postId == intent.post.postId) intent.post else post
            })
        }

        EditPostCase()
    }

    private fun handleCreateCommentIntent(intent: NewsIntent.CreateCommentIntent) {
        updateState { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.postId == intent.comment.postId) {
                        post.copy(comments = post.comments + intent.comment)
                    } else {
                        post
                    }
                }
            )
        }

        CreateCommentCase()
    }

    private fun handleDeleteCommentIntent(intent: NewsIntent.DeleteCommentIntent) {
        updateState { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.postId == intent.comment.postId) {
                        post.copy(comments = post.comments.filter { it.id != intent.comment.id })
                    } else {
                        post
                    }
                }
            )
        }

        DeleteCommentCase()
    }


    private fun handleEditCommentIntent(intent: NewsIntent.EditCommentIntent) {
        updateState { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.postId == intent.comment.postId) {
                        post.copy(comments = post.comments.map { comment ->
                            if (comment.id == intent.comment.id) intent.comment else comment
                        })
                    } else {
                        post
                    }
                }
            )
        }

        EditCommentCase()
    }

    private fun handleRouteProfileIntent(intent: NewsIntent.RouteProfileIntent) {
        //TODO: отправлять в профиль пользователя?

        RouteProfileCase()
    }
}