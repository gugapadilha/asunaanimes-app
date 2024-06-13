package com.example.myanimelist.domain.model

data class Anime(
    val name: String,
    val desc: String,
    val order: Int,
    val photoUrl: String
)