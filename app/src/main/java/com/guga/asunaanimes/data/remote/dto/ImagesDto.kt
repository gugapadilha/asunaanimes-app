package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImagesDto(
    @SerializedName("jpg")
    val jpg: ImageUrlsDto?,
    @SerializedName("webp")
    val webp: ImageUrlsDto?
)

data class ImageUrlsDto(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?
)
