package com.example.myanimelist.data.repository

import com.example.myanimelist.domain.model.Year

class YearRepository {

    fun getAllYears(): List<Year>{
        return listOf(
            Year(
                2024
            ),
            Year(
                2023
            ),
            Year(
                2022
            ),
            Year(
                2021
            ),
            Year(
                2020
            ),
        )
    }
}