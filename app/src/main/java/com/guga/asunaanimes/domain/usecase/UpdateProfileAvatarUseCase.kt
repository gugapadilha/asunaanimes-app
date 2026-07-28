package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.repository.ProfileRepository
import javax.inject.Inject

class UpdateProfileAvatarUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(sourceUri: String): AppResult<Unit> =
        profileRepository.updateAvatar(sourceUri)
}
