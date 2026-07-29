package com.guga.asunaanimes.domain.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun observeAppLanguage(): Flow<AppLanguage>

    fun getAppLanguage(): AppLanguage

    suspend fun setAppLanguage(language: AppLanguage): AppResult<Unit>
}
