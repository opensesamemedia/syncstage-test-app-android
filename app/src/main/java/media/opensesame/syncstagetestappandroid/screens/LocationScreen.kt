package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen

@Composable
fun LocationScreen(
    navController: NavHostController,
    locationViewModel: LocationViewModel = hiltViewModel()
) {

    val loactionUIState by locationViewModel.uiState.collectAsState()


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
                .padding(30.dp)
                .padding(bottom = 50.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Location",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "By default, SyncStage selects the best Studio Server for your session based on measurements.",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Automated selection.",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )
                Switch(
                    checked = loactionUIState.autoSelection,
                    onCheckedChange = { switchOn_ ->
                        locationViewModel.updateAutoSelection(switchOn_)
                    }
                )
            }
            Spacer(modifier = Modifier.height(100.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navController.navigate(SyncStageScreen.Profile.name)
                }) {
                    Text(text = "Previous")
                }

                Button(modifier = Modifier.testTag("location_next_btn"),
                    onClick = {
                        if (loactionUIState.autoSelection) {
                            navController.navigate(SyncStageScreen.LocationLatencies.name)
                        } else {
                            navController.navigate(SyncStageScreen.LocationManual.name)
                        }
                    }) {
                    Text(text = "Next")
                }
            }
        }

    }
}
