package com.guga.asunaanimes.data.local

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {

    fun observeProfile(): Flow<UserProfile>

    suspend fun updateUserName(userName: String): AppResult<Unit>

    suspend fun updateAvatar(sourceUri: String): AppResult<Unit>
}
