package media.opensesame.syncstagetestappandroid.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import media.opensesame.syncstagesdk.models.public.Measurements
import media.opensesame.syncstagetestappandroid.screens.ConnectionModel
import media.opensesame.syncstagetestappandroid.ui.theme.connected_green
import media.opensesame.syncstagetestappandroid.ui.theme.disconnected_red

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
            drawCircle(color = if (connectionModel.isConnected) connected_green else disconnected_red)
        })

        if (!isTransmitter) {
            Text(
                text = "${connectionModel.displayName ?: "Unknown"}",
                modifier = Modifier.weight(1.0f)
            )
            Slider(value = value, valueRange = 0f..100f, onValueChange = {
                onValueChange(it)
            }, modifier = Modifier.width(100.dp))
        } else {
            Text(
                text = "You (${connectionModel.displayName ?: "Unknown"})",
                modifier = Modifier.weight(1.0f)
            )
        }
        val icon = if (connectionModel.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic
        Icon(icon, "Mic")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Quality: ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = colorForQuality(measurements.quality)
                )
            ) {
                append("${measurements.quality} %")
            }
        }, modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Ping: ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = colorForPing(measurements.networkDelayMs)
                )
            ) {
                append("${if (measurements.networkDelayMs != 0) measurements.networkDelayMs else '-'} ms")
            }
        }, modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) {
                append("Jitter: ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = colorForJitter(measurements.networkJitterMs)
                )
            ) {
                append("${if (measurements.networkJitterMs != 0) measurements.networkJitterMs else '-'} ms")
            }
        }, modifier = Modifier.weight(0.33f))
    }
    if (isTransmitter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Text(text = "Network type: $networkType")
        }
    }
    Divider(color = Color.Gray)
}

fun colorForJitter(value: Int): Color {
    return if (value < 5) {
        connected_green
    } else if (value == 5) {
        Color.Yellow
    } else {
        disconnected_red
    }
}

fun colorForPing(value: Int): Color {
    return if (value < 25) {
        connected_green
    } else if (value in 25..34) {
        Color.Yellow
    } else {
        disconnected_red
    }
}

fun colorForQuality(value: Int): Color {
    return if (value >= 80) {
        connected_green
    } else if (value in 50..79) {
        Color.Yellow
    } else {
        disconnected_red
    }
}