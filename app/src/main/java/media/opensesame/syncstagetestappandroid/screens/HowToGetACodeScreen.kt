package media.opensesame.syncstagetestappandroid.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.R
import media.opensesame.syncstagetestappandroid.SyncStageScreen
import media.opensesame.syncstagetestappandroid.ui.theme.JosefinSans


@Composable
fun HowToGetACodeScreen(
    navController: NavHostController,
) {
    val uriHandler = LocalUriHandler.current


    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer, fontFamily = JosefinSans, fontSize = MaterialTheme.typography.bodyLarge.fontSize)) {
            append("To get a provisioning Code you have to be registered in the SyncStage Developer Console ")
        }

        pushStringAnnotation(
            tag = "register",
            annotation = "https://console.sync-stage.com/auth/register"
        )
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontFamily = JosefinSans, fontSize = MaterialTheme.typography.bodyLarge.fontSize)) {
            append("(sign up here).")
        }
        pop()
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onPrimaryContainer, fontFamily = JosefinSans, fontSize = MaterialTheme.typography.bodyLarge.fontSize)) {
            append("\n\nOnce registered you can find it in Developer Console > Applications > Configure.")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 10.dp)
        ) {
            Text(
                text = "How to get a code?",
                style = MaterialTheme.typography.titleLarge
            )

            ClickableText(text = annotatedString, modifier = Modifier.padding(30.dp), onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "register", start = offset, end = offset).firstOrNull()?.let {
                    Log.d("register URL", it.item)
                    uriHandler.openUri(it.item)
                }

            })


            Spacer(modifier = Modifier.height(100.dp))

            Button(modifier = Modifier.testTag("provision_next_btn"),
                onClick = {
                    navController.navigate(SyncStageScreen.Provisioning.name)
                }) {
                Text(text = "I have a code")
            }
        }


    }
}