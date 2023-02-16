package com.example.syncstagetestappandroid.screens

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.syncstagetestappandroid.SyncStageScreen
import com.example.syncstagetestappandroid.components.LoadingIndicator

@Composable
fun CreateJoinSessionScreen(navController: NavHostController, createJoinViewModel: CreateJoinViewModel = hiltViewModel()) ***REMOVED***
    val loginUIState by createJoinViewModel.uiState.collectAsState()
    var isEmptyTextField by remember ***REMOVED*** mutableStateOf(true) ***REMOVED***
    val onSessionCodeChange = ***REMOVED*** text: String ->
        createJoinViewModel.updateSessionCode(text)
        isEmptyTextField = text.isEmpty()
    ***REMOVED***

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) ***REMOVED***
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(30.dp)
                .padding(bottom = 50.dp)
        ) ***REMOVED***
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Sessions",
                textAlign = TextAlign.Left,
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
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
                placeholder = ***REMOVED*** Text(text = "XXX-XXX-XXX") ***REMOVED***,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = ***REMOVED*** // on next.
                        if (loginUIState.sessionCode.isNotEmpty()) ***REMOVED***
                            navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$***REMOVED***loginUIState.sessionCode***REMOVED***")
                        ***REMOVED***
                    ***REMOVED***
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                enabled = loginUIState.loggedIn
            )
            Button(onClick = ***REMOVED***
                if (loginUIState.sessionCode.isNotEmpty()) ***REMOVED***
                    navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$***REMOVED***loginUIState.sessionCode***REMOVED***")
                ***REMOVED***
          ***REMOVED*** enabled = loginUIState.sessionCode.isNotEmpty() && loginUIState.loggedIn) ***REMOVED***
                Text(text = "JOIN")
            ***REMOVED***
            Text(text = "Or")
            Button(onClick = ***REMOVED***
                navController.navigate(SyncStageScreen.Location.name)
          ***REMOVED*** enabled = loginUIState.loggedIn) ***REMOVED***
                Text(text = "NEW SESSION")
            ***REMOVED***
        ***REMOVED***
        if (!loginUIState.loggedIn) ***REMOVED***
            LoadingIndicator()
        ***REMOVED***
    ***REMOVED***

    LaunchedEffect(Unit) ***REMOVED***
        if(!loginUIState.loggedIn) ***REMOVED***
            createJoinViewModel.initiateSyncStage()
        ***REMOVED***
    ***REMOVED***
***REMOVED***