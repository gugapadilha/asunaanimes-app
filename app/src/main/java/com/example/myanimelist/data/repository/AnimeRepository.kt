package com.example.myanimelist.data.repository

import com.example.myanimelist.data.model.Anime

class AnimeRepository {

    fun getAllData(): List<Anime> {
        return listOf(
            Anime(
                photoUrl = "https://www.crunchyroll.com/imgsrv/display/thumbnail/1200x675/catalog/crunchyroll/fdc0dff409f19dfd8ffff5037257ac98.jpe"
            ),
            Anime(
                photoUrl = "https://www.crunchyroll.com/imgsrv/display/thumbnail/1200x675/catalog/crunchyroll/fdc0dff409f19dfd8ffff5037257ac98.jpe"
            ),
            Anime(
                photoUrl = "https://www.crunchyroll.com/imgsrv/display/thumbnail/1200x675/catalog/crunchyroll/fdc0dff409f19dfd8ffff5037257ac98.jpe"
            )

        )
    }

}