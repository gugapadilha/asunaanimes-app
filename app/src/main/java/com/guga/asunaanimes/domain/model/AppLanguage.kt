package com.guga.asunaanimes.domain.model

/**
 * Supported UI languages for the in-app language toggle.
 */
enum class AppLanguage(val tag: String) {
    ENGLISH("en"),
    PORTUGUESE("pt");

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag.equals(tag, ignoreCase = true) } ?: ENGLISH
    }
}
