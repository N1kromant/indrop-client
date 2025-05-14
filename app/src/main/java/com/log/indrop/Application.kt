package com.log.indrop

import android.app.Application
import com.log.indrop.Repo.SearchRepositoryImpl
import com.log.indrop.ViewModels.MessagesViewModel.MessagesViewModel
import com.log.indrop.navigation.NavigationHandlerImpl
import com.log.network.NetworkManager
import com.log.network.ViewModels.MainViewModel
import com.log.network.ViewModels.NetworkViewModel
import com.log.indrop.ViewModels.Search.SearchViewModel
import com.log.indrop.api.SearchApi
import com.log.indrop.api.SearchApiImpl
import com.log.indrop.api.SearchApiTestImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        val appModule = module {

            viewModel { MainViewModel() }
            viewModel { NetworkViewModel() }
            viewModel { SearchViewModel(get()) }
            viewModel { MessagesViewModel() }

            single { NetworkManager() }
            single { NavigationHandlerImpl() }
            single<SearchApi> { SearchApiTestImpl() }
            single { SearchRepositoryImpl(get<SearchApi>()) }

            // Создание нового экземпляра при каждом запросе
//            factory { UseCase(get()) }

        }

        startKoin {
            // Логирование для отладки (можно убрать в релизе)
            androidLogger(Level.ERROR)
            // Контекст Android
            androidContext(this@App)
            // Модули с зависимостями
            modules(appModule)
        }
    }
}
