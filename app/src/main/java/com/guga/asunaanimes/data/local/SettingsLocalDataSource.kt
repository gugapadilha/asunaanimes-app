package com.guga.asunaanimes.data.local

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsLocalDataSource {

    fun observeAppLanguage(): Flow<AppLanguage>

    suspend fun setAppLanguage(language: AppLanguage): AppResult<Unit>

    fun getAppLanguage(): AppLanguage
}
