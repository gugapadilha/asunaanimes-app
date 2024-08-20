package com.guga.myanimelist.domain.model


import com.google.gson.annotations.SerializedName

data class To(
    @SerializedName("day")
    val day: Int,
    @SerializedName("month")
    val month: Int,
    @SerializedName("year")
    val year: Int
)