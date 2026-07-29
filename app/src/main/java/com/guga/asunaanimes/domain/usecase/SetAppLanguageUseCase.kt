package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AppLanguage
import com.guga.asunaanimes.domain.repository.SettingsRepository
import javax.inject.Inject

class SetAppLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(language: AppLanguage): AppResult<Unit> =
        settingsRepository.setAppLanguage(language)
}
