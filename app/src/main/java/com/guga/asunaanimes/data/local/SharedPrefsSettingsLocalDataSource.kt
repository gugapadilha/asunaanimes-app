package com.guga.asunaanimes.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import com.guga.asunaanimes.domain.model.AppLanguage
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

@Singleton
class SharedPrefsSettingsLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SettingsLocalDataSource {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    private val cachedLanguage = MutableStateFlow<AppLanguage?>(null)
    private val writeLock = Mutex()

    override fun observeAppLanguage(): Flow<AppLanguage> = flow {
        ensureLoaded()
        emitAll(cachedLanguage.filterNotNull())
    }

    override fun getAppLanguage(): AppLanguage {
        cachedLanguage.value?.let { return it }
        return readFromDisk().also { cachedLanguage.compareAndSet(null, it) }
    }

    override suspend fun setAppLanguage(language: AppLanguage): AppResult<Unit> =
        writeLock.withLock {
            ensureLoaded()
            runCatching {
                withContext(ioDispatcher) {
                    preferences.edit().putString(KEY_APP_LANGUAGE, language.tag).commit()
                }
                cachedLanguage.value = language
                AppResult.Success(Unit)
            }.getOrElse { error ->
                Log.w(TAG, "Unable to save app language", error)
                AppResult.Failure(AppError.Storage(error))
            }
        }

    private suspend fun ensureLoaded() {
        if (cachedLanguage.value != null) return
        val stored = withContext(ioDispatcher) { readFromDisk() }
        cachedLanguage.compareAndSet(null, stored)
    }

    private fun readFromDisk(): AppLanguage =
        AppLanguage.fromTag(preferences.getString(KEY_APP_LANGUAGE, AppLanguage.ENGLISH.tag))

    private companion object {
        const val TAG = "SettingsStore"
        const val PREFERENCES_FILE_KEY = "settings_prefs"
        const val KEY_APP_LANGUAGE = "app_language"
    }
}
