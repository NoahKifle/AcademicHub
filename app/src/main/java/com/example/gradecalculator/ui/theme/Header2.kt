package com.example.gradecalculator.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gradecalculator.R

@Preview
@Composable
private fun HeaderPreview() {
    Header2()
}

@Composable
fun Header2() {

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Grade Calculator",
            modifier = Modifier
                .background(Color.Black)
                .padding(16.dp),
            color = Color.Green,
            fontSize = 30.sp

        )
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .background(Color.Black)
                .size(67.5.dp, 67.5.dp),
            alignment = Alignment.CenterEnd,
            contentScale = ContentScale.FillWidth
        )
    }
}