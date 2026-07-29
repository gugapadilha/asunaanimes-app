package com.guga.asunaanimes.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guga.asunaanimes.R
import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimeCollectionType
import com.guga.asunaanimes.domain.model.AppLanguage
import com.guga.asunaanimes.domain.usecase.ObserveAnimeCollectionUseCase
import com.guga.asunaanimes.domain.usecase.ObserveAppLanguageUseCase
import com.guga.asunaanimes.domain.usecase.ObserveProfileUseCase
import com.guga.asunaanimes.domain.usecase.SetAppLanguageUseCase
import com.guga.asunaanimes.domain.usecase.UpdateProfileAvatarUseCase
import com.guga.asunaanimes.domain.usecase.UpdateProfileNameUseCase
import com.guga.asunaanimes.presentation.common.UiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeProfile: ObserveProfileUseCase,
    observeAnimeCollection: ObserveAnimeCollectionUseCase,
    observeAppLanguage: ObserveAppLanguageUseCase,
    private val updateProfileName: UpdateProfileNameUseCase,
    private val updateProfileAvatar: UpdateProfileAvatarUseCase,
    private val setAppLanguage: SetAppLanguageUseCase
) : ViewModel() {

    private val draft = MutableStateFlow(ProfileDraft())

    private val _messages = Channel<UiMessage>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()

    val uiState: StateFlow<ProfileUiState> = combine(
        observeProfile(),
        observeAnimeCollection(AnimeCollectionType.WATCHED),
        observeAnimeCollection(AnimeCollectionType.FAVORITE),
        observeAppLanguage(),
        draft
    ) { profile, watched, favorites, language, draftState ->
        ProfileUiState(
            userName = draftState.userName ?: profile.userName,
            avatarUri = profile.avatarUri,
            watchedAnimes = watched,
            favoriteAnimes = favorites,
            watchedCount = watched.size,
            favoriteCount = favorites.size,
            isSavingName = draftState.isSavingName,
            appLanguage = language
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = ProfileUiState()
    )

    fun onUserNameChanged(value: String) {
        draft.update { it.copy(userName = value) }
    }

    fun onSaveName() {
        if (draft.value.isSavingName) return
        val state = uiState.value
        viewModelScope.launch {
            draft.update { it.copy(isSavingName = true) }
            val message = when (val result = updateProfileName(state.userName)) {
                is AppResult.Success -> {
                    draft.value = ProfileDraft()
                    R.string.message_profile_saved
                }
                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to save profile username: ${result.error}")
                    draft.update { it.copy(isSavingName = false) }
                    R.string.message_storage_error
                }
            }
            _messages.send(UiMessage(message))
        }
    }

    fun onAvatarPicked(uri: String) {
        viewModelScope.launch {
            val message = when (val result = updateProfileAvatar(uri)) {
                is AppResult.Success -> R.string.message_profile_avatar_updated
                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to save profile avatar: ${result.error}")
                    R.string.message_storage_error
                }
            }
            _messages.send(UiMessage(message))
        }
    }

    fun onLanguageSelected(language: AppLanguage) {
        viewModelScope.launch {
            when (val result = setAppLanguage(language)) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> {
                    Log.w(TAG, "Unable to change app language: ${result.error}")
                    _messages.send(UiMessage(R.string.message_storage_error))
                }
            }
        }
    }

    private data class ProfileDraft(
        val userName: String? = null,
        val isSavingName: Boolean = false
    )

    private companion object {
        const val TAG = "ProfileViewModel"
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
