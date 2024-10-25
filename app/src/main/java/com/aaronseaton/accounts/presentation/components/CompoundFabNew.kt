package com.aaronseaton.accounts.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CompoundFabNew(
    modifier: Modifier = Modifier,
    name: String,
    icon: ImageVector,
    fabs: List<Pair<()->Unit, String>>
){
    var showThreeButtons by remember { mutableStateOf(false) }
    val fabModifier = modifier
        .width(160.dp)
        .padding(2.dp)
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (showThreeButtons) {
            fabs.map { pair ->
                ExtendedFloatingActionButton(
                    onClick = pair.first,
                    modifier = fabModifier.align(Alignment.Start)
                ) {
                    Text(pair.second)
                }
            }
        }
        ExtendedFloatingActionButton(
            text = { Text(name) },
            icon = { Icon(icon, null) },
            onClick = { showThreeButtons = !showThreeButtons },
            modifier = fabModifier.align(Alignment.Start)
        )
    }
}