package com.movieapp.ui.movie_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.movieapp.ui.theme.netflix_gray

@Composable
fun BottomNavItem(
    imageVector: ImageVector,
    onScreenChange:()->Unit,
    isSelected: Boolean
){
    Column(
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }) {
            onScreenChange()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector,
            contentDescription = null,
            tint = if (isSelected) Color.White else netflix_gray
        )
    }
}