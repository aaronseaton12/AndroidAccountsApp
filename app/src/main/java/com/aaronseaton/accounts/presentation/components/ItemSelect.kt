package com.aaronseaton.accounts.presentation.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.aaronseaton.accounts.presentation.customer.SearchAppBar

@Composable
fun <T> ItemSelect(
    items: List<T>,
    isDialogShowing: Boolean,
    onDismissRequest: () -> Unit,
    filterFunction: (List<T>, String) -> List<T>,
    cardText: (T) -> String,
    onItemSelected: (T) -> Unit,
){
    var searchAppText by remember { mutableStateOf("") }

    if (isDialogShowing) {
        Dialog(onDismissRequest) {
            Column {
                Row {
                    SearchAppBar(
                        text = searchAppText,
                        onTextChange = { searchAppText = it },
                        onCloseClicked = { onDismissRequest() },
                        onSearchClicked = { Log.d("Searched Text", it) }
                    )
                }
                LazyColumn(Modifier.padding(bottom = 60.dp)) {

                    val filteredCustomers = filterFunction(items, searchAppText)
                    items(filteredCustomers) { item ->
                        ItemCard(
                            item,
                            onItemSelected,
                            onDismissRequest,
                            cardText,
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun <T> ItemCard(
    item:T,
    onItemSelected: (T) -> Unit,
    onDismissRequest: () -> Unit,
    cardText: (T)->String,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemSelected(item)
                onDismissRequest()
            }
    ) {
        Column(Modifier.padding(5.dp)) {

            Text(
                text = cardText(item),
                modifier = Modifier.padding(horizontal = 5.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        }
    }
}

