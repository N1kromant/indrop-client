package com.log.indrop

import android.app.Application
import com.log.network.NetworkManager
import com.log.network.ViewModels.MainViewModel
import com.log.network.ViewModels.NetworkViewModel
import com.log.network.ViewModels.Search.SearchViewModel
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
            viewModel { SearchViewModel() }

            single { NetworkManager() }

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
