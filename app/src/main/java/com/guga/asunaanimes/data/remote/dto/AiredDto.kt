package com.guga.asunaanimes.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AiredDto(
    @SerializedName("from")
    val from: String?,
    @SerializedName("to")
    val to: String?
)
