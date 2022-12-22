package com.example.syncstagetestappandroid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize().clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = {}), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.background(Color(0.1f,0.1f,0.1f,0.3f), shape = RoundedCornerShape(10.0f)).size(100.dp,100.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}