package com.guga.asunaanimes.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.guga.asunaanimes.R
import com.guga.asunaanimes.presentation.theme.AsunaOnSurface
import com.guga.asunaanimes.presentation.theme.AsunaOnSurfaceMuted
import com.guga.asunaanimes.presentation.theme.AsunaOrange
import com.guga.asunaanimes.presentation.theme.AsunaSearchField
import com.guga.asunaanimes.presentation.theme.AsunaSearchFieldText
import com.guga.asunaanimes.presentation.theme.AsunaSurfaceElevated

@Composable
fun SearchBox(
    onSearch: (String) -> Unit,
    previousSearches: List<String>,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    var suggestions by remember { mutableStateOf(emptyList<String>()) }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun submit(query: String) {
        onSearch(query)
        keyboardController?.hide()
        suggestions = emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    suggestions = if (newText.isBlank()) {
                        emptyList()
                    } else {
                        previousSearches.filter { it.startsWith(newText, ignoreCase = true) }
                    }
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = AsunaSearchFieldText.copy(alpha = 0.55f),
                        maxLines = 1
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AsunaOrange
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = {
                            text = ""
                            suggestions = emptyList()
                            submit("")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.search_clear),
                                tint = AsunaSearchFieldText.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AsunaSearchField,
                    unfocusedContainerColor = AsunaSearchField,
                    disabledContainerColor = AsunaSearchField,
                    focusedTextColor = AsunaSearchFieldText,
                    unfocusedTextColor = AsunaSearchFieldText,
                    cursorColor = AsunaOrange,
                    focusedBorderColor = AsunaOrange,
                    unfocusedBorderColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submit(text) })
            )

            AsunaPrimaryButton(
                text = stringResource(R.string.search_action),
                onClick = { submit(text) },
                modifier = Modifier.height(56.dp)
            )
        }

        if (suggestions.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(14.dp),
                color = AsunaSurfaceElevated.copy(alpha = 0.94f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    items(items = suggestions, key = { it }) { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    text = suggestion
                                    submit(suggestion)
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.content_description_previous_search),
                                tint = AsunaOnSurfaceMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = suggestion,
                                color = AsunaOnSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
