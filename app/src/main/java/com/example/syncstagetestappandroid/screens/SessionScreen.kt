package com.example.syncstagetestappandroid.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.syncstagetestappandroid.SyncStageScreen
import com.example.syncstagetestappandroid.components.LoadingIndicator
import com.example.syncstagetestappandroid.components.UserConnection

@Composable
fun SessionScreen(navController: NavHostController, sessionCode: String, sessionViewModel: SessionViewModel = hiltViewModel()) ***REMOVED***
    val sessionUIState by sessionViewModel.uiState.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var popupControl by remember ***REMOVED*** mutableStateOf(false) ***REMOVED***
    var showLoadingIndicator by remember ***REMOVED*** mutableStateOf(false) ***REMOVED***

    sessionViewModel.sessionLeft = ***REMOVED***
        showLoadingIndicator = false
        navController.popBackStack()
    ***REMOVED***

    BackHandler ***REMOVED***
        sessionViewModel.leaveSession()
    ***REMOVED***

    Box(modifier = Modifier.fillMaxSize()) ***REMOVED***
        Column ***REMOVED***
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) ***REMOVED***

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center, modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) ***REMOVED***
                    Text(
                        text = "Participants",
                        style = TextStyle(fontSize = 24.sp),
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp, top = 20.dp)
                    )
                    sessionUIState.connections.let ***REMOVED***
                        it.forEach ***REMOVED*** connectionModel ->
                            val isTransmitter = sessionViewModel.transmitterIdentifier == connectionModel.identifier
                            var value: Float = 0.0f
                            if (!isTransmitter) ***REMOVED***
                                value = sessionViewModel.getReceiverVolume(identifier = connectionModel.identifier).toFloat()
                            ***REMOVED***
                            val measurements = sessionViewModel.getMeasurements(identifier = connectionModel.identifier)
                            UserConnection(connectionModel = connectionModel,
                                measurements = measurements,
                                networkType = sessionUIState.networkType,
                                isTransmitter,
                                value = value,
                                onValueChange = ***REMOVED*** volume ->
                                    sessionViewModel.changeReceiverVolume(connectionModel.identifier, volume)
                                ***REMOVED***
                            )
                        ***REMOVED***
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(158.dp),
                contentAlignment = Alignment.Center
            ) ***REMOVED***
                Column(horizontalAlignment = Alignment.CenterHorizontally) ***REMOVED***
                    Text(
                        "Invite others",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                    )
                    Text(
                        buildAnnotatedString ***REMOVED***
                            withStyle(style = SpanStyle(fontSize = 13.sp, color = Color.Gray)) ***REMOVED***
                                append("Share this code with others: ")
                            ***REMOVED***
                            withStyle(style = SpanStyle(fontSize = 15.sp)) ***REMOVED***
                                append(sessionCode)
                            ***REMOVED***
                      ***REMOVED***
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                            .padding(bottom = 15.dp)
                    )
                    Button(onClick = ***REMOVED***
                        clipboardManager.setText(AnnotatedString(sessionCode))
                  ***REMOVED*** modifier = Modifier.padding(bottom = 10.dp)) ***REMOVED***
                        Icon(
                            Icons.Filled.FileCopy, "contentDescription",
                        )
                        Text(text = "COPY JOINING CODE")
                    ***REMOVED***
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) ***REMOVED***
                        Button(
                            onClick = ***REMOVED***
                                showLoadingIndicator = true
                                sessionViewModel.leaveSession()
                          ***REMOVED***
                            modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) ***REMOVED***
                            Icon(Icons.Filled.CallEnd, "contentDescription")
                        ***REMOVED***
                        Button(
                            onClick = ***REMOVED***
                                sessionViewModel.toggleMicrophone(!sessionViewModel.isMuted)
                          ***REMOVED*** modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) ***REMOVED***
                            val icon = if(sessionViewModel.isMuted) ***REMOVED*** Icons.Filled.MicOff ***REMOVED*** else ***REMOVED*** Icons.Filled.Mic ***REMOVED***
                            Icon(icon, "Mute")
                        ***REMOVED***
                        Button(
                            onClick = ***REMOVED***
                                popupControl = true
                          ***REMOVED*** modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) ***REMOVED***
                            Icon(Icons.Filled.Menu, "contentDescription")
                        ***REMOVED***
                    ***REMOVED***
                ***REMOVED***
                if(popupControl) ***REMOVED***
                    Popup(
                        //alignment = Alignment.Center,
                        onDismissRequest = ***REMOVED*** popupControl = false ***REMOVED***
                    ) ***REMOVED***
                        Column(modifier = Modifier
                            .shadow(5.dp, shape = RoundedCornerShape(5.dp), clip = false)
                            .background(color = Color.White)
                            .fillMaxWidth()
                            .padding(20.dp)) ***REMOVED***
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start) ***REMOVED***
                                Text(text = "Direct Monitor", modifier = Modifier.padding(end = 20.dp))
                                Switch(checked = sessionViewModel.isDirectMonitorEnabled, onCheckedChange = ***REMOVED***
                                    sessionViewModel.toggleDirectMonitor(it)
                                ***REMOVED***)
                            ***REMOVED***
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start) ***REMOVED***
                                Text(text = "Internal Microphone", modifier = Modifier.padding(end = 20.dp))
                                Switch(checked = sessionViewModel.isInternalMicrophoneEnabled, onCheckedChange = ***REMOVED***
                                    sessionViewModel.toggleInternalMicrophone(it)
                                ***REMOVED***)
                            ***REMOVED***
                            Row(modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start) ***REMOVED***
                                Text(text = "Direct Monitor volume", modifier = Modifier.padding(end = 20.dp))
                                Slider(value = sessionUIState.directMonitorVolume, onValueChange = ***REMOVED***
                                    sessionViewModel.changeDirectMonitorVolume(it)
                              ***REMOVED*** Modifier.width(100.dp))
                            ***REMOVED***
                            Text(text = "Note: A headphone required to enable the direct monitor feature.")
                        ***REMOVED***
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
        if (sessionUIState.session == null || showLoadingIndicator) ***REMOVED***
            LoadingIndicator()
        ***REMOVED***
    ***REMOVED***

    LaunchedEffect(Unit) ***REMOVED***
        if(sessionUIState.session == null) ***REMOVED***
            sessionViewModel.joinSession(
                sessionCode = sessionCode,
            )
            sessionViewModel.initiate5GDetection()
        ***REMOVED***
    ***REMOVED***
***REMOVED***