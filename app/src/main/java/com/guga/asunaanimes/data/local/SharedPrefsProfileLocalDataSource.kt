package com.guga.asunaanimes.data.local

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.core.coroutines.IoDispatcher
import com.guga.asunaanimes.domain.model.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
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
 * Stores the profile username in SharedPreferences and copies picked avatars into app-private
 * files so the photo survives beyond the temporary gallery content URI.
 */
@Singleton
class SharedPrefsProfileLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileLocalDataSource {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    private val cachedProfile = MutableStateFlow<UserProfile?>(null)
    private val writeLock = Mutex()

    override fun observeProfile(): Flow<UserProfile> = flow {
        ensureLoaded()
        emitAll(cachedProfile.filterNotNull())
    }

    override suspend fun updateUserName(userName: String): AppResult<Unit> =
        writeLock.withLock {
            ensureLoaded()
            runCatching {
                withContext(ioDispatcher) {
                    preferences.edit()
                        .putString(KEY_USER_NAME, userName)
                        .remove(KEY_LEGACY_FIRST_NAME)
                        .remove(KEY_LEGACY_LAST_NAME)
                        .apply()
                }
                cachedProfile.value = currentProfile().copy(userName = userName)
                AppResult.Success(Unit)
            }.getOrElse { error ->
                Log.w(TAG, "Unable to save profile username", error)
                AppResult.Failure(AppError.Storage(error))
            }
        }

    override suspend fun updateAvatar(sourceUri: String): AppResult<Unit> =
        writeLock.withLock {
            ensureLoaded()
            runCatching {
                val persistedUri = withContext(ioDispatcher) {
                    persistAvatar(Uri.parse(sourceUri))
                }
                preferences.edit().putString(KEY_AVATAR_URI, persistedUri).apply()
                cachedProfile.value = currentProfile().copy(avatarUri = persistedUri)
                AppResult.Success(Unit)
            }.getOrElse { error ->
                Log.w(TAG, "Unable to save profile avatar", error)
                AppResult.Failure(AppError.Storage(error))
            }
        }

    private suspend fun ensureLoaded() {
        if (cachedProfile.value != null) return
        val stored = withContext(ioDispatcher) { readFromDisk() }
        cachedProfile.compareAndSet(null, stored)
    }

    private fun readFromDisk(): UserProfile {
        val storedUserName = preferences.getString(KEY_USER_NAME, null)
        val userName = when {
            !storedUserName.isNullOrBlank() -> storedUserName
            else -> listOf(
                preferences.getString(KEY_LEGACY_FIRST_NAME, "").orEmpty(),
                preferences.getString(KEY_LEGACY_LAST_NAME, "").orEmpty()
            ).map { it.trim() }.filter { it.isNotEmpty() }.joinToString(" ")
        }

        return UserProfile(
            userName = userName,
            avatarUri = preferences.getString(KEY_AVATAR_URI, null)
        )
    }

    private fun currentProfile(): UserProfile = cachedProfile.value ?: UserProfile()

    private fun persistAvatar(sourceUri: Uri): String {
        val avatarDir = File(context.filesDir, AVATAR_DIR).apply { mkdirs() }
        avatarDir.listFiles()?.forEach { previous -> previous.delete() }
        val destination = File(avatarDir, "avatar_${System.currentTimeMillis()}.jpg")
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IOException("Unable to open avatar source: $sourceUri")
        return destination.toURI().toString()
    }

    private companion object {
        const val TAG = "ProfileStore"
        const val PREFERENCES_FILE_KEY = "profile_prefs"
        const val KEY_USER_NAME = "user_name"
        const val KEY_LEGACY_FIRST_NAME = "first_name"
        const val KEY_LEGACY_LAST_NAME = "last_name"
        const val KEY_AVATAR_URI = "avatar_uri"
        const val AVATAR_DIR = "profile"
    }
}
