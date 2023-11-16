package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@Composable
fun WelcomeScreen(
    navController: NavHostController,
) {

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
                text = "Welcome",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                buildAnnotatedString {
                    append("To use the SyncStage Test Application you need to be registered as a SyncStage Developer or have a ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Provisioning Code.")
                    }
                },
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(100.dp))

            Button(
                modifier = Modifier.testTag("provisioning_btn"),
                onClick = {
                    navController.navigate(SyncStageScreen.Provisioning.name)
                }) {
                Text(text = "I have a Provisioning Code")
            }

            Text(
                text = "or",
            )

            Spacer(modifier = Modifier.size(10.dp))
            Button(
                modifier = Modifier.testTag("how_to_get_provisioning_btn"),
                onClick = {
                    navController.navigate(SyncStageScreen.HowToGetACode.name)
                }) {
                Text(text = "How to get a Provisioning Code")
            }

        }

    }
}