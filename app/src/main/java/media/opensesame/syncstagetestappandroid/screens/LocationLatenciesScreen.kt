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
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen
import media.opensesame.syncstagetestappandroid.components.LoadingIndicator

@Composable
fun LocationLatenciesScreen(
    navController: NavHostController,
    locationLatenciesViewModel: LocationLatenciesViewModel = hiltViewModel()
) {
    val locationLatenciesUIState by locationLatenciesViewModel.uiState.collectAsState()

//    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
//    var mExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(30.dp)
                .padding(bottom = 50.dp)
                .fillMaxSize()
        ) {
            Column() {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Location",
                    textAlign = TextAlign.Left,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Network latency to different Studio Servers.",
                    textAlign = TextAlign.Left,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                locationLatenciesUIState.results.map {
                    val selected =
                        locationLatenciesUIState.selectedStudioInstance?.zoneName == it.name

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = it.name,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = "${it.latency} ms",
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Divider(color = Color.Gray)
                }

            }
            Spacer(modifier = Modifier.height(100.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navController.navigate(SyncStageScreen.Location.name)
                }) {
                    Text(text = "Previous")
                }

                Button(
                    onClick = {
                        navController.navigate(SyncStageScreen.CreateJoinSession.name)
                    },
                    enabled = locationLatenciesUIState.selectedStudioInstance != null
                ) {
                    Text(text = "Next")
                }
            }
        }
        if (locationLatenciesUIState.results.isEmpty()) {
            LoadingIndicator()
        }
    }

    LaunchedEffect(Unit) {
        if (locationLatenciesUIState.results.isEmpty()) {
            locationLatenciesViewModel.getServerInstances()
        }
    }
}
