package com.guga.asunaanimes.domain.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    fun observeProfile(): Flow<UserProfile>

    suspend fun updateUserName(userName: String): AppResult<Unit>

    /**
     * Persists a picked gallery image into app storage and updates the profile avatar.
     *
     * @param sourceUri content URI from the system photo picker.
     */
    suspend fun updateAvatar(sourceUri: String): AppResult<Unit>
}
