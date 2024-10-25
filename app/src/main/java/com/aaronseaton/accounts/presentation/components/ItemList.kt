package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp

inline fun <reified T> LazyListScope.itemList(
    list: List<T>,
    listType:String,
    crossinline listCard: @Composable (T)->Unit
){
    if (list.isEmpty()) {
        item {
            Box(
                modifier = Modifier.fillParentMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "No $listType listed",
                    modifier = Modifier.padding(50.dp),
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme
                        .onSurface.copy(alpha = 0.65f)
                )
            }
        }
    }
    else {
        items(list) {
            listCard(it)
        }
    }
}