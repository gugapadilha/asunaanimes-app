package com.guga.asunaanimes.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import com.guga.asunaanimes.data.local.model.AnimeEntity
import com.guga.asunaanimes.data.mapper.toDomainOrNull
import com.guga.asunaanimes.data.mapper.toEntity
import com.guga.asunaanimes.data.remote.dto.AnimeDto
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Stores the favorite/watched lists in [SharedPreferences] and exposes them as hot flows so every
 * screen observing a collection stays in sync after a change.
 */
@Singleton
class SharedPrefsAnimeCollectionLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AnimeCollectionLocalDataSource {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    // `null` means "not read from disk yet".
    private val cachedCollections: Map<AnimeCollectionType, MutableStateFlow<List<AnimeEntity>?>> =
        AnimeCollectionType.values().associateWith { MutableStateFlow(null) }

    private val writeLock = Mutex()

    override fun observe(type: AnimeCollectionType): Flow<List<AnimeEntity>> = flow {
        ensureLoaded(type)
        emitAll(cacheOf(type).filterNotNull())
    }

    override suspend fun add(type: AnimeCollectionType, anime: AnimeEntity): Boolean =
        writeLock.withLock {
            ensureLoaded(type)
            val current = cacheOf(type).value.orEmpty()
            if (current.any { it.malId == anime.malId }) return@withLock false
            persist(type, current + anime)
            true
        }

    override suspend fun remove(type: AnimeCollectionType, malId: Int) = writeLock.withLock {
        ensureLoaded(type)
        val current = cacheOf(type).value.orEmpty()
        val updated = current.filterNot { it.malId == malId }
        if (updated.size != current.size) persist(type, updated)
    }

    override suspend fun updateUserScore(
        type: AnimeCollectionType,
        malId: Int,
        userScore: Int?
    ): Boolean = writeLock.withLock {
        ensureLoaded(type)
        val current = cacheOf(type).value.orEmpty()
        val index = current.indexOfFirst { it.malId == malId }
        if (index < 0) return@withLock false
        val updated = current.toMutableList().also { list ->
            list[index] = list[index].copy(userScore = userScore)
        }
        persist(type, updated)
        true
    }

    private suspend fun ensureLoaded(type: AnimeCollectionType) {
        val cache = cacheOf(type)
        if (cache.value != null) return
        val stored = withContext(ioDispatcher) { readFromDisk(type) }
        // Another coroutine may have won the race; the value read from disk is identical anyway.
        cache.compareAndSet(null, stored)
    }

    private suspend fun persist(type: AnimeCollectionType, animes: List<AnimeEntity>) {
        withContext(ioDispatcher) {
            preferences.edit().putString(type.storageKey, gson.toJson(animes)).apply()
        }
        cacheOf(type).value = animes
    }

    private fun readFromDisk(type: AnimeCollectionType): List<AnimeEntity> {
        preferences.getString(type.storageKey, null)?.let { json ->
            return json.parseAs(ENTITY_LIST_TYPE) ?: emptyList()
        }
        return migrateLegacyCollection(type)
    }

    /**
     * Version 1 stored the raw Jikan payload. It is converted once and rewritten in the current
     * format so users do not lose their lists after the migration.
     */
    private fun migrateLegacyCollection(type: AnimeCollectionType): List<AnimeEntity> {
        val legacyJson = preferences.getString(type.legacyStorageKey, null) ?: return emptyList()
        val migrated = legacyJson.parseAs<List<AnimeDto>>(LEGACY_LIST_TYPE)
            .orEmpty()
            .mapNotNull { it.toDomainOrNull()?.toEntity() }
        preferences.edit()
            .putString(type.storageKey, gson.toJson(migrated))
            .remove(type.legacyStorageKey)
            .apply()
        return migrated
    }

    private fun <T> String.parseAs(type: Type): T? = runCatching {
        gson.fromJson<T>(this, type)
    }.onFailure {
        Log.w(TAG, "Unable to read stored anime collection", it)
    }.getOrNull()

    private fun cacheOf(type: AnimeCollectionType) = requireNotNull(cachedCollections[type])

    private val AnimeCollectionType.storageKey: String
        get() = when (this) {
            AnimeCollectionType.FAVORITE -> "favorite_animes_v2"
            AnimeCollectionType.WATCHED -> "watched_animes_v2"
        }

    private val AnimeCollectionType.legacyStorageKey: String
        get() = when (this) {
            AnimeCollectionType.FAVORITE -> "favorite_animes"
            AnimeCollectionType.WATCHED -> "watched_animes"
        }

    private companion object {
        const val TAG = "AnimeCollectionStore"
        const val PREFERENCES_FILE_KEY = "com.guga.myanimelist.PREFERENCE_FILE_KEY"
        val ENTITY_LIST_TYPE = object : TypeToken<List<AnimeEntity>>() {}.type
        val LEGACY_LIST_TYPE = object : TypeToken<List<AnimeDto>>() {}.type
    }
}
