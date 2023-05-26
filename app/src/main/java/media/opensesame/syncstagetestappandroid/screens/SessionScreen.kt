package media.opensesame.syncstagetestappandroid.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import media.opensesame.syncstagetestappandroid.components.LoadingIndicator
import media.opensesame.syncstagetestappandroid.components.UserConnection
import media.opensesame.syncstagetestappandroid.networkutils.decodeNetworkType

@SuppressLint("MissingPermission")
@Composable
fun SessionScreen(
    navController: NavHostController,
    sessionCode: String,
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    val sessionUIState by sessionViewModel.uiState.collectAsState()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var popupControl by remember { mutableStateOf(false) }
    var showLoadingIndicator by remember { mutableStateOf(false) }

    val telephony by sessionViewModel.telephonyType.collectAsState()
    val networkType: String? =  if (Build.VERSION.SDK_INT >= 30) {
        decodeNetworkType(telephony, sessionViewModel.context.get())
    } else {
        null
    }

    sessionViewModel.sessionLeft = {
        CoroutineScope(Dispatchers.Main).launch {
            showLoadingIndicator = false
            navController.popBackStack()
        }
    }

    BackHandler {
        sessionViewModel.leaveSession()
    }

    LaunchedEffect(key1 = sessionViewModel){
        sessionViewModel.startForegroundService()
    }

    DisposableEffect(key1 = sessionViewModel) {
        onDispose {
            sessionViewModel.stopForegroundService()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Box(
                modifier = Modifier
                    .weight(1.0f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter,
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center, modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Participants",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp, top = 20.dp)
                    )

                    sessionUIState.connections.let {
                        it.forEach { connectionModel ->
                            val isTransmitter =
                                sessionViewModel.transmitterIdentifier == connectionModel.identifier
                            var value = 0.0f
                            if (!isTransmitter) {
                                value =
                                    sessionViewModel.getReceiverVolume(identifier = connectionModel.identifier)
                                        .toFloat()
                            }
                            val measurements =
                                sessionViewModel.getMeasurements(identifier = connectionModel.identifier)


                            UserConnection(connectionModel = connectionModel,
                                measurements = measurements,
                                networkType = networkType ?: sessionUIState.networkTypeOldApi,
                                isTransmitter,
                                value = value,
                                onValueChange = { volume ->
                                    sessionViewModel.changeReceiverVolume(
                                        connectionModel.identifier,
                                        volume
                                    )
                                }
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(158.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Invite others",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                    )
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontSize = 13.sp, color = Color.Gray)) {
                                append("Share this code with others: ")
                            }
                            withStyle(style = SpanStyle(fontSize = 15.sp)) {
                                append(sessionCode)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 30.dp)
                            .padding(bottom = 15.dp)
                    )
                    Button(onClick = {
                        clipboardManager.setText(AnnotatedString(sessionCode))
                    }, modifier = Modifier.padding(bottom = 10.dp)) {
                        Icon(
                            Icons.Filled.FileCopy, "contentDescription",
                        )
                        Text(text = "COPY JOINING CODE")
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                showLoadingIndicator = true
                                sessionViewModel.leaveSession()
                            },
                            modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) {
                            Icon(Icons.Filled.CallEnd, "contentDescription")
                        }
                        Button(
                            onClick = {
                                sessionViewModel.toggleMicrophone(!sessionViewModel.isMuted)
                            }, modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) {
                            val icon = if (sessionViewModel.isMuted) {
                                Icons.Filled.MicOff
                            } else {
                                Icons.Filled.Mic
                            }
                            Icon(icon, "Mute")
                        }
                        Button(
                            onClick = {
                                popupControl = true
                            }, modifier = Modifier
                                .weight(33.3f)
                                .fillMaxHeight(),
                            shape = RectangleShape
                        ) {
                            Icon(Icons.Filled.Menu, "contentDescription")
                        }
                    }
                }
                if (popupControl) {
                    Popup(
                        onDismissRequest = { popupControl = false }
                    ) {
                        Column(
                            modifier = Modifier
                                .shadow(5.dp, shape = RoundedCornerShape(5.dp), clip = false)
                                .background(color = MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Direct Monitor",
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                                Switch(
                                    checked = sessionViewModel.isDirectMonitorEnabled,
                                    onCheckedChange = {
                                        sessionViewModel.toggleDirectMonitor(it)
                                    })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Internal Microphone",
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                                Switch(
                                    checked = sessionViewModel.isInternalMicrophoneEnabled,
                                    onCheckedChange = {
                                        sessionViewModel.toggleInternalMicrophone(it)
                                    })
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "Direct Monitor volume",
                                    modifier = Modifier.padding(end = 20.dp)
                                )
                                Slider(value = sessionUIState.directMonitorVolume, onValueChange = {
                                    sessionViewModel.changeDirectMonitorVolume(it)
                                }, Modifier.width(100.dp))
                            }
                            Text(text = "Note: A headphone required to enable the direct monitor feature.")
                        }
                    }
                }
            }
        }
        if (sessionUIState.session == null || showLoadingIndicator) {
            LoadingIndicator()
        }
    }

    LaunchedEffect(Unit) {
        if (sessionUIState.session == null) {
            sessionViewModel.joinSession(
                sessionCode = sessionCode,
            )
        }
    }
}