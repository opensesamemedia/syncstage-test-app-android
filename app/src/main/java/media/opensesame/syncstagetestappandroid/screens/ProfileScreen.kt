package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@Composable
fun ProfileScreen(navController: NavHostController, profileViewModel: ProfileViewModel = hiltViewModel()) ***REMOVED***
    val profileUIState by profileViewModel.uiState.collectAsState()

    val onUserNameChange = ***REMOVED*** text : String ->
        profileViewModel.updateUserName(text)
    ***REMOVED***
    val focusRequester = FocusRequester()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) ***REMOVED***
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) ***REMOVED***
            Text(text = "This is the name that will be displayed for other users when joining a session.",
                modifier = Modifier.padding(30.dp), textAlign = TextAlign.Center)
            OutlinedTextField(
                value = profileUIState.userName,
                singleLine = true,
                onValueChange = onUserNameChange,
                label = ***REMOVED*** Text(text = "Please enter your name")***REMOVED***,
                //isError = profileUIState.userName.isNullOrEmpty(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = ***REMOVED*** // on next.
                        onNextClick(profileUIState.userName, navController, viewModel = profileViewModel)
                    ***REMOVED***
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(bottom = 10.dp)
            )
            Button(onClick = ***REMOVED***
                onNextClick(profileUIState.userName, navController, viewModel = profileViewModel)
          ***REMOVED*** enabled = profileUIState.userName.isNotEmpty()) ***REMOVED***
                Text(text = "NEXT")
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    LaunchedEffect(Unit) ***REMOVED***
        focusRequester.requestFocus()
    ***REMOVED***
***REMOVED***

fun onNextClick(userName: String, navController: NavHostController, viewModel: ProfileViewModel) ***REMOVED***
    if (userName.isNotEmpty()) ***REMOVED***
        viewModel.createUserId()
        navController.navigate(SyncStageScreen.CreateJoinSession.name)
    ***REMOVED***
***REMOVED***
