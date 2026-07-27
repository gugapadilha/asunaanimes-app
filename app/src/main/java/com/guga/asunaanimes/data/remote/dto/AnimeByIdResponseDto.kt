package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnimeByIdResponseDto(
    @SerializedName("data")
    val data: AnimeDto?
)
