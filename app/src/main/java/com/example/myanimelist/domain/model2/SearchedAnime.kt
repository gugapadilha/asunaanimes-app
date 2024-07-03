package com.example.myanimelist.domain.model2


import com.google.gson.annotations.SerializedName

data class SearchedAnime(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("pagination")
    val pagination: Pagination
)