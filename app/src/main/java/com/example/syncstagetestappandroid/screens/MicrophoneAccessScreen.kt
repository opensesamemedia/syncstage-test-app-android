package com.example.syncstagetestappandroid.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.syncstagetestappandroid.SyncStageScreen
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneAccessScreen(navController: NavHostController) {
    val permissions = listOf(android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

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
            Icon(imageVector = Icons.Filled.Mic, contentDescription = "Microphone", modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Please enable phone call management access so you can adjust the volume level in the session and use a speaker without experiencing an unpleasant echo.",
                modifier = Modifier.padding(30.dp), textAlign = TextAlign.Center)
            Button(onClick = {
                if (permissionsState.allPermissionsGranted) {
                    navController.navigate(SyncStageScreen.Profile.name)
                } else {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }) {
                val title =
                    if (permissionsState.allPermissionsGranted) "NEXT" else "ALLOW ACCESS"
                Text(text = title)
            }
        }
    }
}