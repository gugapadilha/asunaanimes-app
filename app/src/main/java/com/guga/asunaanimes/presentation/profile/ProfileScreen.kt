package com.guga.asunaanimes.presentation.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.guga.asunaanimes.R
import com.guga.asunaanimes.domain.model.Anime
import com.guga.asunaanimes.domain.model.AppLanguage
import com.guga.asunaanimes.presentation.common.UiMessageEffect
import com.guga.asunaanimes.presentation.components.AnimatedBackground
import com.guga.asunaanimes.presentation.components.AsunaPrimaryButton
import com.guga.asunaanimes.presentation.components.CollectionScreenHeader
import com.guga.asunaanimes.presentation.components.FloatingBottomNavClearance
import com.guga.asunaanimes.presentation.theme.AsunaBlack
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiMessageEffect(messages = viewModel.messages)

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.onAvatarPicked(it.toString()) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(imageRes = R.drawable.home_screen)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = FloatingBottomNavClearance)
        ) {
            CollectionScreenHeader(title = stringResource(R.string.profile_screen_header))

            ProfileIdentitySection(
                avatarUri = uiState.avatarUri,
                userName = uiState.userName,
                onChangePhotoClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileUserNameForm(
                userName = uiState.userName,
                isSaving = uiState.isSavingName,
                onUserNameChanged = viewModel::onUserNameChanged,
                onSaveClick = viewModel::onSaveName
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileLanguageSection(
                selectedLanguage = uiState.appLanguage,
                onLanguageSelected = viewModel::onLanguageSelected
            )

            Spacer(modifier = Modifier.height(28.dp))

            ProfileCollectionPreview(
                title = stringResource(R.string.profile_watched_section),
                count = uiState.watchedCount,
                animes = uiState.watchedAnimes,
                emptyText = stringResource(R.string.profile_watched_empty)
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileCollectionPreview(
                title = stringResource(R.string.profile_favorites_section),
                count = uiState.favoriteCount,
                animes = uiState.favoriteAnimes,
                emptyText = stringResource(R.string.profile_favorites_empty)
            )
        }
    }
}

@Composable
private fun ProfileIdentitySection(
    avatarUri: String?,
    userName: String,
    onChangePhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .border(width = 2.dp, color = AsunaOrange, shape = CircleShape)
                .background(AsunaSurfaceElevated)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onChangePhotoClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (avatarUri.isNullOrBlank()) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = stringResource(R.string.content_description_profile_photo),
                    tint = AsunaOnSurfaceMuted,
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = avatarUri),
                    contentDescription = stringResource(R.string.content_description_profile_photo),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AsunaOrange),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = stringResource(R.string.profile_change_photo),
                    tint = AsunaBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = userName.ifBlank { stringResource(R.string.profile_guest_name) },
            color = AsunaOnSurface,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.profile_change_photo_hint),
            color = AsunaOnSurfaceMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ProfileUserNameForm(
    userName: String,
    isSaving: Boolean,
    onUserNameChanged: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = AsunaSurfaceElevated.copy(alpha = 0.92f),
            tonalElevation = 0.dp,
            shadowElevation = 3.dp
        ) {
            BasicTextField(
                value = userName,
                onValueChange = onUserNameChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 4.dp)
                    .heightIn(min = 38.dp),
                singleLine = true,
                textStyle = TextStyle(
                    color = AsunaOnSurface,
                    fontSize = 15.sp
                ),
                cursorBrush = SolidColor(AsunaOrange),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onSaveClick()
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (userName.isEmpty()) {
                            Text(
                                text = stringResource(R.string.profile_username_placeholder),
                                color = AsunaOnSurfaceMuted,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        AsunaPrimaryButton(
            text = stringResource(
                if (isSaving) R.string.profile_saving else R.string.profile_save
            ),
            onClick = onSaveClick,
            fontSize = 14.sp,
            minHeight = 44.dp,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileLanguageSection(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_language_section),
            color = AsunaOnSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.profile_language_hint),
            color = AsunaOnSurfaceMuted,
            fontSize = 12.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LanguageOptionChip(
                label = stringResource(R.string.profile_language_english),
                selected = selectedLanguage == AppLanguage.ENGLISH,
                onClick = { onLanguageSelected(AppLanguage.ENGLISH) },
                modifier = Modifier.weight(1f)
            )
            LanguageOptionChip(
                label = stringResource(R.string.profile_language_portuguese),
                selected = selectedLanguage == AppLanguage.PORTUGUESE,
                onClick = { onLanguageSelected(AppLanguage.PORTUGUESE) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LanguageOptionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) AsunaOrange else AsunaSurfaceElevated.copy(alpha = 0.92f),
        tonalElevation = 0.dp,
        shadowElevation = if (selected) 0.dp else 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (selected) AsunaBlack else AsunaOnSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileCollectionPreview(
    title: String,
    count: Int,
    animes: List<Anime>,
    emptyText: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = AsunaOnSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.profile_anime_count, count),
                color = AsunaOrange,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (animes.isEmpty()) {
            Text(
                text = emptyText,
                color = AsunaOnSurfaceMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                animes.forEach { anime ->
                    CompactAnimePoster(anime = anime)
                }
            }
        }
    }
}

@Composable
private fun CompactAnimePoster(anime: Anime) {
    Column(
        modifier = Modifier.width(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = anime.imageUrl),
            contentDescription = stringResource(R.string.content_description_anime_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(118.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AsunaSurfaceElevated)
        )
        Text(
            text = anime.title,
            color = AsunaOnSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}
