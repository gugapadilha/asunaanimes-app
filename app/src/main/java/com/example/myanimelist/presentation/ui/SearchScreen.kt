import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.SearchBox
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(navController: NavHostController) {
    val animeList = remember { mutableStateListOf<Data>() }
    val painter = rememberAsyncImagePainter(R.drawable.search_screen)
    val animeService = AnimeService.create()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    suspend fun loadPage(page: Int) {
        try {
            val topAnime = withContext(Dispatchers.IO) { animeService.getTopAnime(page) }
            val animeListFromApi = topAnime.data
            animeList.addAll(animeListFromApi)
            Log.d("SearchScreen", "Animes Received: ${animeListFromApi.size}")
        } catch (e: Exception) {
            Log.e("SearchScreen", "Error to obtain top anime: ${e.message}")
        }
    }

    suspend fun searchAnime(query: String) {
        try {
            val searchedAnime = withContext(Dispatchers.IO) { animeService.getSearchedAnime(query) }
            animeList.clear()
            animeList.addAll(searchedAnime.data)
            Log.d("SearchScreen", "Search Animes Received: ${searchedAnime.data.size}")
        } catch (e: Exception) {
            Log.e("SearchScreen", "Error to search anime: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            loadPage(1)
            loadPage(2)
        }
    }

    fun loadMoreAnimes() {
        val nextPage = animeList.size / 25 + 1
        coroutineScope.launch {
            loadPage(nextPage)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                if (lastVisibleItemIndex == animeList.size - 1) {
                    loadMoreAnimes()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SearchBox { query ->
                coroutineScope.launch {
                    searchAnime(query)
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                state = listState
            ) {
                items(animeList.chunked(3)) { rowItems ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (anime in rowItems) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            ) {
                                AnimeItem(anime = anime)
                            }
                        }
                    }
                }
            }
        }
    }
}
