package com.guga.asunaanimes.domain.model

/**
 * Local "account" profile kept on-device. Avatar is a persisted file path/URI string, not a
 * remote account photo.
 */
data class UserProfile(
    val userName: String = "",
    val avatarUri: String? = null
)
