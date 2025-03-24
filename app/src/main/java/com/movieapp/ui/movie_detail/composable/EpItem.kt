package com.movieapp.ui.movie_detail.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.movieapp.R

@Composable
fun EpItem(ep: String, epSelected: String, onItemSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp,40.dp)
            .background(color = colorResource(R.color.netflix_white_30), shape = RoundedCornerShape(5.dp))
            .clickable {
                onItemSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (ep.contains(Regex("\\d"))) ep.split(" ").last().toString() else ep,
            style = MaterialTheme.typography.titleSmall,
            color = if (ep == epSelected) colorResource(R.color.netflix_red2) else Color.White,
            textDecoration = if (ep == epSelected) TextDecoration.Underline else TextDecoration.None
        )
    }
}