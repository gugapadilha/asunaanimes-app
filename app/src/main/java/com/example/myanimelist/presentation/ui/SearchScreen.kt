import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.myanimelist.R
import com.example.myanimelist.data.service.AnimeService
import com.example.myanimelist.domain.model2.Data
import com.example.myanimelist.presentation.ui.bottomsheet.AnimeDetailsBottomSheet
import com.example.myanimelist.presentation.util.AnimeItem
import com.example.myanimelist.presentation.util.SearchBox
import com.example.myanimelist.presentation.util.loadPreviousSearches
import com.example.myanimelist.presentation.util.saveSearchQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    val animeList = remember { mutableStateListOf<Data>() }
    val painter = rememberAsyncImagePainter(R.drawable.search_screen)
    val animeService = AnimeService.create()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    var selectedAnime by remember { mutableStateOf<Data?>(null) }
    var previousSearches by remember { mutableStateOf<List<String>>(listOf()) }
    val context = LocalContext.current

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

    suspend fun searchAnime(query: String): List<Data> {
        return try {
            val searchedAnime = withContext(Dispatchers.IO) { animeService.getSearchedAnime(query) }
            searchedAnime.data
        } catch (e: Exception) {
            Log.e("SearchScreen", "Error to search anime: ${e.message}")
            emptyList()
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            loadPage(1)
            loadPage(2)
            loadPage(3)
            loadPage(4)
            previousSearches = loadPreviousSearches(context) // Carregar pesquisas anteriores
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

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            selectedAnime?.let { AnimeDetailsBottomSheet(anime = it) }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painter,
                contentDescription = "Background Image",
                contentScale = ContentScale.FillBounds
            )

            Column(modifier = Modifier.fillMaxSize()) {
                SearchBox(
                    onSearch = { query ->
                        coroutineScope.launch {
                            if (query.isBlank()) {
                                animeList.clear()
                                loadPage(1)
                                loadPage(2)
                                loadPage(3)
                                loadPage(4)
                            } else {
                                val searchResults = searchAnime(query)
                                animeList.clear()
                                animeList.addAll(searchResults)
                                saveSearchQuery(query, context) // save last search
                                previousSearches = loadPreviousSearches(context) // update latest searches
                            }
                        }
                    },
                    previousSearches = previousSearches
                )

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
                                        .clickable {
                                            selectedAnime = anime
                                            coroutineScope.launch {
                                                bottomSheetState.show()
                                            }
                                        }
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
}