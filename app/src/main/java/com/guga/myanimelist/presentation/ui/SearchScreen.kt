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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.guga.myanimelist.R
import com.guga.myanimelist.data.service.AnimeService
import com.guga.myanimelist.domain.model.Data
import com.guga.myanimelist.presentation.ui.bottomsheet.AnimeDetailsBottomSheet
import com.guga.myanimelist.presentation.ui.viewmodel.SearchViewModel
import com.guga.myanimelist.presentation.util.AnimeItem
import com.guga.myanimelist.presentation.util.SearchBox
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
    val context = LocalContext.current
    val searchViewModel: SearchViewModel = viewModel()
    var isLoading by remember { mutableStateOf(true) }

    suspend fun loadPage(page: Int) {
        try {
            val topAnime = withContext(Dispatchers.IO) { animeService.getTopAnime(page) }
            val animeListFromApi = topAnime.data
            animeList.addAll(animeListFromApi)
            Log.d("SearchScreen", "Animes Received: ${animeListFromApi.size}")
        } catch (e: Exception) {
            Log.e("SearchScreen", "Error to obtain top anime: ${e.message}")
        } finally {
            isLoading = false
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
            searchViewModel.loadPreviousSearchesFromStorage(context)
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: 0
                if (lastVisibleItemIndex == animeList.size - 1) {
                    val nextPage = animeList.size / 25 + 1
                    loadPage(nextPage)
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
                Spacer(modifier = Modifier.padding(top = 15.dp))

                SearchBox(
                    onSearch = { query ->
                        coroutineScope.launch {
                            if (query.isBlank()) {
                                animeList.clear()
                                isLoading = true
                                loadPage(1)
                                loadPage(2)
                                loadPage(3)
                                loadPage(4)
                            } else {
                                val searchResults = searchAnime(query)
                                animeList.clear()
                                animeList.addAll(searchResults)
                                searchViewModel.saveSearchQueryToStorage(query, context)
                                isLoading = false
                            }
                        }
                    },
                    previousSearches = searchViewModel.previousSearches.value
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState
                        ) {
                            items(animeList.chunked(3)) { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    rowItems.forEach { anime ->
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
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f).padding(4.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}