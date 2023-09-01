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
import androidx.compose.ui.platform.testTag
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
    val createJoinUIState by createJoinViewModel.uiState.collectAsState()
    var showLoadingIndicator by remember { mutableStateOf(false) }

    var isEmptyTextField by remember { mutableStateOf(true) }
    val onSessionCodeChange = { text: String ->
        createJoinViewModel.updateSessionCode(text)
        isEmptyTextField = text.isEmpty()
    }

    createJoinViewModel.createSessionCallback = { sessionCode ->
        showLoadingIndicator = false
        navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$sessionCode")
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
                value = createJoinUIState.sessionCode,
                singleLine = true,
                onValueChange = onSessionCodeChange,
                placeholder = { Text(text = "XXX-XXX-XXX") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { // on next.
                        if (createJoinUIState.sessionCode.isNotEmpty()) {
                            navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=${createJoinUIState.sessionCode}")
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .testTag("session_code"),
                )
            Button(modifier = Modifier.testTag("join_btn"),
                onClick = {
                if (createJoinUIState.sessionCode.isNotEmpty()) {
                    navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=${createJoinUIState.sessionCode}")
                }
            }, enabled = createJoinUIState.sessionCode.isNotEmpty()) {
                Text(text = "JOIN")
            }
            Text(text = "Or")
            Button(modifier = Modifier.testTag("new_session_btn"),
                onClick = {
                showLoadingIndicator = true
                createJoinViewModel.createNewSession()
            }) {
                Text(text = "NEW SESSION")
            }
        }

    }
    if (showLoadingIndicator) {
        LoadingIndicator()
    }


}