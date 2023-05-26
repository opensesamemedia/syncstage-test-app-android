package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen
import media.opensesame.syncstagetestappandroid.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJoinSessionScreen(
    navController: NavHostController,
    createJoinViewModel: CreateJoinViewModel = hiltViewModel()
) {
    val loginUIState by createJoinViewModel.uiState.collectAsState()
    var isEmptyTextField by remember { mutableStateOf(true) }
    val onSessionCodeChange = { text: String ->
        createJoinViewModel.updateSessionCode(text)
        isEmptyTextField = text.isEmpty()
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
                .padding(30.dp)
                .padding(bottom = 50.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Sessions",
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Enter a code to join an existing session or create a new one.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                textAlign = TextAlign.Left
            )
            OutlinedTextField(
                value = loginUIState.sessionCode,
                singleLine = true,
                onValueChange = onSessionCodeChange,
                placeholder = { Text(text = "XXX-XXX-XXX") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { // on next.
                        if (loginUIState.sessionCode.isNotEmpty()) {
                            navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=${loginUIState.sessionCode}")
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                enabled = loginUIState.loggedIn
            )
            Button(onClick = {
                if (loginUIState.sessionCode.isNotEmpty()) {
                    navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=${loginUIState.sessionCode}")
                }
            }, enabled = loginUIState.sessionCode.isNotEmpty() && loginUIState.loggedIn) {
                Text(text = "JOIN")
            }
            Text(text = "Or")
            Button(onClick = {
                navController.navigate(SyncStageScreen.Location.name)
            }, enabled = loginUIState.loggedIn) {
                Text(text = "NEW SESSION")
            }
        }
        if (!loginUIState.loggedIn) {
            LoadingIndicator()
        }
    }

    LaunchedEffect(Unit) {
        if (!loginUIState.loggedIn) {
            createJoinViewModel.initiateSyncStage()
        }
    }
}