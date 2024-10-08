package com.guga.myanimelist.domain.model


import com.google.gson.annotations.SerializedName

data class Trailer(
    @SerializedName("embed_url")
    val embedUrl: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("youtube_id")
    val youtubeId: String
)