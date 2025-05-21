package com.log.indrop

import android.app.Application
import android.app.NotificationManager
import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.indrop.ViewModels.MessagesViewModel.MessagesViewModel
import com.log.indrop.ViewModels.News.NewsState
import com.log.indrop.ViewModels.News.NewsViewModel
import com.log.indrop.navigation.NavigationHandlerImpl
import com.log.network.NetworkManager
import com.log.network.ViewModels.MainViewModel
import com.log.network.ViewModels.NetworkViewModel
import com.log.indrop.ViewModels.Search.SearchViewModel
import com.log.indrop.api.SearchApi
import com.log.indrop.api.SearchApiImpl
import com.log.indrop.api.SearchApiTestImpl
import com.log.indrop.data.storage.UserPreferences
import com.log.indrop.domain.services.notification.NotificationIntegrationManager
import com.log.indrop.domain.services.notification.NotificationService
import com.log.indrop.domain.subscription.MessageSubscriptionHandler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val notificationModule = module {
            single { NotificationService(androidContext()) }
            // UserPreferences для хранения данных пользователя
            single { UserPreferences(androidContext()) }
            // Обработчик подписки на уведомления о сообщениях
            factory { MessageSubscriptionHandler(androidContext()) }
            // Интеграционный менеджер для связи с ViewModel
            //single { NotificationIntegrationManager(androidContext()) }
            factory { NotificationIntegrationManager(androidContext()) }
        }

        val appModule = module {

            viewModel { NewsViewModel() }

            single { NetworkManager() }
            single { NavigationHandlerImpl() }
            single<SearchApi> { SearchApiImpl(get()) }
            single { SearchRepositoryImpl(get<SearchApi>()) }

            single { MainViewModel() }
            single { NetworkViewModel() }
            single { SearchViewModel(get()) }
            single { MessagesViewModel() }

            // Создание нового экземпляра при каждом запросе
//            factory { UseCase(get()) }

        }

        startKoin {
            // Логирование для отладки (можно убрать в релизе)
            androidLogger(Level.ERROR)
            // Контекст Android
            androidContext(this@App)
            // Модули с зависимостями
            modules(listOf(
                appModule,
                notificationModule
            ))
        }
    }
}
