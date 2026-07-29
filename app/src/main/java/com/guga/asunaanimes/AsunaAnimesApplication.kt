package com.guga.asunaanimes

import android.app.Application
import com.guga.asunaanimes.core.locale.AppLocaleManager
import com.guga.asunaanimes.domain.repository.SettingsRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AsunaAnimesApplication : Application() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var appLocaleManager: AppLocaleManager

    override fun onCreate() {
        super.onCreate()
        appLocaleManager.apply(settingsRepository.getAppLanguage())
    }
}
