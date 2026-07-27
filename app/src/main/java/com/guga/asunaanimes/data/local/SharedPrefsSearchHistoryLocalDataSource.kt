package com.guga.asunaanimes.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * Keeps the most recent search queries, newest first.
 *
 * Version 1 stored them in a [Set], which has no defined iteration order, so the "most recent"
 * list was effectively random. They are now stored as an ordered JSON array.
 */
@Singleton
class SharedPrefsSearchHistoryLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SearchHistoryLocalDataSource {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    private val cachedQueries = MutableStateFlow<List<String>?>(null)
    private val writeLock = Mutex()

    override fun observeRecentQueries(): Flow<List<String>> = flow {
        ensureLoaded()
        emitAll(cachedQueries.filterNotNull())
    }

    override suspend fun saveQuery(query: String) = writeLock.withLock {
        ensureLoaded()
        val updated = buildList {
            add(query)
            addAll(cachedQueries.value.orEmpty().filterNot { it.equals(query, ignoreCase = true) })
        }.take(MAX_RECENT_QUERIES)

        withContext(ioDispatcher) {
            preferences.edit().putString(RECENT_QUERIES_KEY, gson.toJson(updated)).apply()
        }
        cachedQueries.value = updated
    }

    private suspend fun ensureLoaded() {
        if (cachedQueries.value != null) return
        val stored = withContext(ioDispatcher) { readFromDisk() }
        cachedQueries.compareAndSet(null, stored)
    }

    private fun readFromDisk(): List<String> {
        preferences.getString(RECENT_QUERIES_KEY, null)?.let { json ->
            return runCatching { gson.fromJson<List<String>>(json, QUERY_LIST_TYPE) }
                .onFailure { Log.w(TAG, "Unable to read stored search history", it) }
                .getOrNull()
                .orEmpty()
        }
        return migrateLegacyQueries()
    }

    private fun migrateLegacyQueries(): List<String> {
        val legacyQueries = preferences.getStringSet(LEGACY_QUERIES_KEY, null) ?: return emptyList()
        val migrated = legacyQueries.toList().take(MAX_RECENT_QUERIES)
        preferences.edit()
            .putString(RECENT_QUERIES_KEY, gson.toJson(migrated))
            .remove(LEGACY_QUERIES_KEY)
            .apply()
        return migrated
    }

    private companion object {
        const val TAG = "SearchHistoryStore"
        const val PREFERENCES_FILE_KEY = "search_prefs"
        const val RECENT_QUERIES_KEY = "recent_queries_v2"
        const val LEGACY_QUERIES_KEY = "queries"
        const val MAX_RECENT_QUERIES = 5
        val QUERY_LIST_TYPE = object : TypeToken<List<String>>() {}.type
    }
}
