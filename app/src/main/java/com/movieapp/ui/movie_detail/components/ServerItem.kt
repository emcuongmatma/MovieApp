package com.movieapp.ui.movie_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movieapp.ui.theme.netflix_red
import com.movieapp.ui.theme.netflix_white_30

@Composable
fun ServerItem(name: String, index: Int, serverSelected: Int, onItemSelected: (Int) -> Unit) {
    val modifier = if (index == serverSelected) Modifier
        .background(
            color = netflix_white_30,
            shape = RoundedCornerShape(5.dp)
        )
        .wrapContentWidth()
        .clickable {
            onItemSelected(index)
        } else
        Modifier
            .wrapContentWidth()
            .clickable {
                onItemSelected(index)
            }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = name,
            style = MaterialTheme.typography.titleSmall,
            color = if (index == serverSelected) netflix_red else Color.White,
            textAlign = TextAlign.Center
        )
    }
}
