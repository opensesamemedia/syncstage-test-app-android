package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@Composable
fun HowToGetACodeScreen(
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
                text = "How to get a code?",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                buildAnnotatedString {
                    append("To get a provisioning Code you have to be registered in the SyncStage Developer Console ")
                    pushStringAnnotation(
                        tag = "register",
                        annotation = "https://console.sync-stage.com/auth/register"
                    )
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("(sign up here).")
                    }
                    pop()

                    append("\n\nOnce registered you can find it in Developer Console > Applications > Configure.")
                },
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Left
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navController.navigate(SyncStageScreen.Welcome.name)
                }) {
                    Text(text = "Previous")
                }

                Button(modifier = Modifier.testTag("provision_next_btn"),
                    onClick = {
                        navController.navigate(SyncStageScreen.Provisioning.name)
                    }) {
                    Text(text = "I have a code")
                }
            }

        }

    }
}