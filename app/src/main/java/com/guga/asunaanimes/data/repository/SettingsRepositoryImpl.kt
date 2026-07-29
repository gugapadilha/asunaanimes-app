package com.guga.asunaanimes.data.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.core.locale.AppLocaleManager
import com.guga.asunaanimes.data.local.SettingsLocalDataSource
import com.guga.asunaanimes.domain.model.AppLanguage
import com.guga.asunaanimes.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val localDataSource: SettingsLocalDataSource,
    private val appLocaleManager: AppLocaleManager
) : SettingsRepository {

    override fun observeAppLanguage(): Flow<AppLanguage> = localDataSource.observeAppLanguage()

    override fun getAppLanguage(): AppLanguage = localDataSource.getAppLanguage()

    override suspend fun setAppLanguage(language: AppLanguage): AppResult<Unit> {
        val result = localDataSource.setAppLanguage(language)
        if (result is AppResult.Success) {
            appLocaleManager.apply(language)
        }
        return result
    }
}
