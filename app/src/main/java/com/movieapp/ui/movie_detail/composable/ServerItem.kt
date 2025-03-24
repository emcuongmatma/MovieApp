package com.movieapp.ui.movie_detail.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ServerItem(name: String, index: Int, serverSelected: Int, onItemSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .clickable {
                onItemSelected(index)
            }
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = if (index == serverSelected) Color(229, 9, 19) else Color.White
        )
    }
}
