package media.opensesame.syncstagetestappandroid.components

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
import media.opensesame.syncstagesdk.models.public.Measurements
import media.opensesame.syncstagetestappandroid.screens.ConnectionModel

@Composable
fun UserConnection(
    connectionModel: ConnectionModel,
    measurements: Measurements,
    networkType: String,
    isTransmitter: Boolean,
    value: Float,
    onValueChange: (value: Float) -> Unit
) ***REMOVED***
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) ***REMOVED***
        Canvas(modifier = Modifier
            .size(30.dp)
            .padding(end = 10.dp), onDraw = ***REMOVED***
            drawCircle(color = if (connectionModel.isConnected) Color.Green else Color.Red)
        ***REMOVED***)

        if (!isTransmitter) ***REMOVED***
            Text(text = "$***REMOVED***connectionModel.displayName ?: "Unknown"***REMOVED***", modifier = Modifier.weight(1.0f))
            Slider(value = value, valueRange = 0f..100f, onValueChange = ***REMOVED***
                onValueChange(it)
          ***REMOVED*** modifier = Modifier.width(100.dp))
        ***REMOVED***else ***REMOVED***
            Text(text = "You ($***REMOVED***connectionModel.displayName ?: "Unknown"***REMOVED***)", modifier = Modifier.weight(1.0f))
        ***REMOVED***
        val icon = if(connectionModel.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic
        Icon(icon, "Mic")
    ***REMOVED***
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 10.dp)) ***REMOVED***
        Text(buildAnnotatedString ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) ***REMOVED***
                append("Quality: ")
            ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForQuality(measurements.quality))) ***REMOVED***
                append("$***REMOVED***measurements.quality***REMOVED*** %")
            ***REMOVED***
      ***REMOVED*** modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) ***REMOVED***
                append("Ping: ")
            ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForPing(measurements.networkDelayMs))) ***REMOVED***
                append("$***REMOVED***if (measurements.networkDelayMs != 0) measurements.networkDelayMs else '-'***REMOVED*** ms")
            ***REMOVED***
      ***REMOVED*** modifier = Modifier.weight(0.33f))
        Text(buildAnnotatedString ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp)) ***REMOVED***
                append("Jitter: ")
            ***REMOVED***
            withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 13.sp, color = colorForJitter(measurements.networkJitterMs))) ***REMOVED***
                append("$***REMOVED***if (measurements.networkJitterMs != 0) measurements.networkJitterMs else '-'***REMOVED*** ms")
            ***REMOVED***
      ***REMOVED*** modifier = Modifier.weight(0.33f))
    ***REMOVED***
    if(isTransmitter) ***REMOVED***
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)) ***REMOVED***
            Text(text = "Network type: $networkType")
        ***REMOVED***
    ***REMOVED***
    Divider(color = Color.Gray)
***REMOVED***

fun colorForJitter(value: Int):Color ***REMOVED***
    return if (value < 5) ***REMOVED***
        Color.Green
    ***REMOVED*** else if (value == 5) ***REMOVED***
        Color.Yellow
    ***REMOVED*** else ***REMOVED***
        Color.Red
    ***REMOVED***
***REMOVED***

fun colorForPing(value: Int):Color ***REMOVED***
    return if (value < 25) ***REMOVED***
        Color.Green
    ***REMOVED*** else if (value in 25..34) ***REMOVED***
        Color.Yellow
    ***REMOVED*** else ***REMOVED***
        Color.Red
    ***REMOVED***
***REMOVED***

fun colorForQuality(value: Int):Color ***REMOVED***
    return if (value >= 80) ***REMOVED***
        Color.Green
    ***REMOVED*** else if (value in 50..79) ***REMOVED***
        Color.Yellow
    ***REMOVED*** else ***REMOVED***
        Color.Red
    ***REMOVED***
***REMOVED***