package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.R
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@Composable
fun IntroScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()) {
            Image(painter = painterResource(id = R.drawable.syncstage), contentDescription = "", modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "SyncStage Example Application", fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 17.sp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("SyncStage")
                    }
                    append(" is a patent-pending voice chat platform that allows you to sing, jam, learn, win together with audio latency lower ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("than ever before.")
                    }
                },
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center
            )

            Button(onClick = {
                navController.navigate(SyncStageScreen.Access.name)
            }) {
                Text(text = "START")
            }

        }

    }
}