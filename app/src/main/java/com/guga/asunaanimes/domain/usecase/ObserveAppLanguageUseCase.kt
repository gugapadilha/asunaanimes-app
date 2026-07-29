package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.model.AppLanguage
import com.guga.asunaanimes.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveAppLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppLanguage> = settingsRepository.observeAppLanguage()
}
