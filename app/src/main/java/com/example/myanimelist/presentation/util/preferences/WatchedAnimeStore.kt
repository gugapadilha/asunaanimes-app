package com.example.myanimelist.presentation.util.preferences

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.myanimelist.domain.model2.Data

object WatchedAnimeStore {
    private val _watchedAnimeList = mutableStateListOf<Data>()
    val watchedAnimeList: SnapshotStateList<Data> = _watchedAnimeList

    fun addAnime(anime: Data, context: Context) {
        _watchedAnimeList.add(anime)
        SharedPreferencesHelper.saveWatchedAnimes(context, _watchedAnimeList.toList())
    }

    fun removeAnime(anime: Data, context: Context) {
        _watchedAnimeList.remove(anime)
        SharedPreferencesHelper.saveWatchedAnimes(context, _watchedAnimeList.toList())
    }

    fun loadWatchedAnimes(context: Context) {
        val animes = SharedPreferencesHelper.getWatchedAnimes(context)
        _watchedAnimeList.clear()
        _watchedAnimeList.addAll(animes)
    }

    fun isAnimeInList(anime: Data, context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("anime_prefs", Context.MODE_PRIVATE)
        val savedAnimes = sharedPreferences.getStringSet("watched_animes", mutableSetOf()) ?: mutableSetOf()
        return savedAnimes.contains(anime.title)
    }
}
