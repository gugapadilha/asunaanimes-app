package com.guga.asunaanimes.data.repository

import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.data.local.AnimeCollectionLocalDataSource
import com.guga.asunaanimes.data.mapper.toDomain
import com.guga.asunaanimes.data.mapper.toEntity
import com.guga.asunaanimes.domain.model.AddToCollectionOutcome
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.repository.AnimeCollectionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AnimeCollectionRepositoryImpl @Inject constructor(
    private val localDataSource: AnimeCollectionLocalDataSource
) : AnimeCollectionRepository {

    override fun observeCollection(type: AnimeCollectionType): Flow<List<Anime>> =
        localDataSource.observe(type).map { entities -> entities.map { it.toDomain() } }

    override suspend fun add(
        type: AnimeCollectionType,
        anime: Anime
    ): AppResult<AddToCollectionOutcome> = runStorageCatching {
        if (localDataSource.add(type, anime.toEntity())) {
            AddToCollectionOutcome.ADDED
        } else {
            AddToCollectionOutcome.ALREADY_IN_COLLECTION
        }
    }

    override suspend fun remove(type: AnimeCollectionType, anime: Anime): AppResult<Unit> =
        runStorageCatching { localDataSource.remove(type, anime.malId) }

    private inline fun <T> runStorageCatching(block: () -> T): AppResult<T> = try {
        AppResult.Success(block())
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        AppResult.Failure(AppError.Storage(throwable))
    }
}
