package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val profileUIState by profileViewModel.uiState.collectAsState()

    val onUserNameChange = { text: String ->
        profileViewModel.updateUserName(text)
    }
    val focusRequester = FocusRequester()

    profileViewModel.onLogout = {
        CoroutineScope(Dispatchers.Main).launch {
            navController.navigate(SyncStageScreen.Intro.name)
        }}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "This is the name that will be displayed for other users when joining a session.",
                modifier = Modifier.padding(30.dp), textAlign = TextAlign.Center
            )
            OutlinedTextField(
                value = profileUIState.userName,
                singleLine = true,
                onValueChange = onUserNameChange,
                label = { Text(text = "Please enter your name") },
                //isError = profileUIState.userName.isNullOrEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { // on next.
                        onNextClick(
                            profileUIState.userName,
                            navController,
                            viewModel = profileViewModel
                        )
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(bottom = 10.dp)
                    .testTag("username_input")

            )
            Button(modifier = Modifier.testTag("next_btn"),
                onClick = {
                onNextClick(profileUIState.userName, navController, viewModel = profileViewModel)
            }, enabled = profileUIState.userName.isNotEmpty()) {
                Text(text = "Next")
            }

            Button(modifier = Modifier.testTag("logout_btn"),
                onClick = {
                    profileViewModel.logout()
                }) {
                Text(text = "Log out")
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

fun onNextClick(userName: String, navController: NavHostController, viewModel: ProfileViewModel) {
    if (userName.isNotEmpty()) {
        viewModel.createUserId()
        navController.navigate(SyncStageScreen.Location.name)
    }
}
