package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.domain.model.UserProfile
import com.guga.asunaanimes.domain.repository.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {

    operator fun invoke(): Flow<UserProfile> = profileRepository.observeProfile()
}
