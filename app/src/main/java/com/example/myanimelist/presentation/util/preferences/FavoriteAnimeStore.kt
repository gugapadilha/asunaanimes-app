package com.example.myanimelist.presentation.util.preferences

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.myanimelist.domain.model2.Data

object FavoriteAnimeStore {
    private val _favoriteAnimeList = mutableStateListOf<Data>()
    val favoriteAnimeList: SnapshotStateList<Data> = _favoriteAnimeList

    fun addAnime(anime: Data, context: Context) {
        _favoriteAnimeList.add(anime)
        SharedPreferencesHelper.saveFavoriteAnimes(context, _favoriteAnimeList.toList())
    }

    fun removeAnime(anime: Data, context: Context) {
        _favoriteAnimeList.remove(anime)
        SharedPreferencesHelper.saveFavoriteAnimes(context, _favoriteAnimeList.toList())
    }

    fun loadFavoriteAnimes(context: Context) {
        val animes = SharedPreferencesHelper.getFavoriteAnimes(context)
        _favoriteAnimeList.clear()
        _favoriteAnimeList.addAll(animes)
    }

    fun isAnimeInList(anime: Data, context: Context): Boolean {
        return _favoriteAnimeList.any { it.title == anime.title }
    }

}