package com.movieapp.ui.movie_list.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.movieapp.R
import com.movieapp.ui.theme.netflix_black

@Composable
fun SourceManagerDialog(onDismissRequest: () -> Unit, onSource: (Int) -> Unit) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
        }
    ) {
        Column(
            modifier = Modifier.background(color = netflix_black, shape = RoundedCornerShape(10.dp)).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Lựa chọn nguồn phim",
                style = MaterialTheme.typography.titleLarge.copy(Color.White),
                modifier = Modifier.padding(top = 10.dp),
                fontWeight = FontWeight.Bold
            )
            Image(
                painter = painterResource(R.drawable.logokkphim),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                        onSource(0)
                        onDismissRequest()
                    },
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(R.drawable.logoophim),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .fillMaxWidth()
                    .clickable (indication = null, interactionSource = remember { MutableInteractionSource() }){
                        onSource(1)
                        onDismissRequest()
                    },
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(R.drawable.logonc),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .fillMaxWidth()
                    .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
//                        onSource(2)
//                        onDismissRequest()
                    },
                contentScale = ContentScale.Fit
            )
        }
    }
}


