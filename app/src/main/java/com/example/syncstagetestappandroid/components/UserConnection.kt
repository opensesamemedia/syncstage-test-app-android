package com.example.syncstagetestappandroid.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.syncstagetestappandroid.screens.ConnectionModel
import media.opensesame.syncstagesdk.models.public.Measurements

@Composable
fun UserConnection(
    connectionModel: ConnectionModel,
    measurements: Measurements,
    networkType: String,
    isTransmitter: Boolean,
    value: Float,
    onValueChange: (value: Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier
            .size(30.dp)
            .padding(end = 10.dp), onDraw = {
            drawCircle(color = if (connectionModel.isConnected) Color.Green else Color.Red)
        })

        if (!isTransmitter) {
            Text(text = "${connectionModel.displayName ?: "Unknown"}", modifier = Modifier.weight(1.0f))
            Slider(value = value, valueRange = 0f..100f, onValueChange = {
                onValueChange(it)
            }, modifier = Modifier.width(100.dp))
        }else {
            Text(text = "You (${connectionModel.displayName ?: "Unknown"})", modifier = Modifier.weight(1.0f))
        }
        val icon = if(connectionModel.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic
        Icon(icon, "Mic")
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Quality: ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForQuality(measurements.quality))) {
                append("${measurements.quality} %")
            }
        }, modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Ping: ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForPing(measurements.networkDelayMs))) {
                append("${measurements.networkDelayMs} ms")
            }
        }, modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Jitter: ")
            }
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForJitter(measurements.networkJitterMs))) {
                append("${measurements.networkJitterMs} ms")
            }
        }, modifier = Modifier.weight(0.33f))
    }
    if(isTransmitter) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)) {
            Text(text = "Network type: $networkType")
        }
    }
    Divider(color = Color.Gray)
}

fun colorForJitter(value: Int):Color {
    return if (value < 5) {
        Color.Green
    } else if (value == 5) {
        Color.Yellow
    } else {
        Color.Red
    }
}

fun colorForPing(value: Int):Color {
    return if (value < 25) {
        Color.Green
    } else if (value in 25..34) {
        Color.Yellow
    } else {
        Color.Red
    }
}

fun colorForQuality(value: Int):Color {
    return if (value >= 80) {
        Color.Green
    } else if (value in 50..79) {
        Color.Yellow
    } else {
        Color.Red
    }
}