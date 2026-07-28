package com.guga.asunaanimes.data.repository

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.data.local.ProfileLocalDataSource
import com.guga.asunaanimes.domain.model.UserProfile
import com.guga.asunaanimes.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val localDataSource: ProfileLocalDataSource
) : ProfileRepository {

    override fun observeProfile(): Flow<UserProfile> = localDataSource.observeProfile()

    override suspend fun updateUserName(userName: String): AppResult<Unit> =
        localDataSource.updateUserName(userName)

    override suspend fun updateAvatar(sourceUri: String): AppResult<Unit> =
        localDataSource.updateAvatar(sourceUri)
}
