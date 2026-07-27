package com.guga.asunaanimes.domain.usecase

import com.guga.asunaanimes.core.common.AppResult
import com.guga.asunaanimes.domain.model.AnimePage
import com.guga.asunaanimes.domain.repository.AnimeRepository
import javax.inject.Inject

class GetSeasonalAnimeUseCase @Inject constructor(
    private val animeRepository: AnimeRepository
) {

    suspend operator fun invoke(page: Int): AppResult<AnimePage> =
        animeRepository.getSeasonalAnime(page.coerceAtLeast(FIRST_PAGE))

    companion object {
        const val FIRST_PAGE = 1
    }
}
