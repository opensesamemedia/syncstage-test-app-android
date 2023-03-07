package media.opensesame.syncstagetestappandroid.screens

import android.os.Build
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
import com.google.accompanist.permissions.*
import media.opensesame.syncstagetestappandroid.SyncStageScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneAccessScreen(navController: NavHostController) ***REMOVED***
    val permissions = mutableListOf(android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.INTERNET,
                            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.ACCESS_WIFI_STATE,
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)***REMOVED***
        permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
    ***REMOVED***

    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

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
            Icon(imageVector = Icons.Filled.Mic, contentDescription = "Microphone", modifier = Modifier.size(60.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Please enable phone call management access so you can adjust the volume level in the session and use a speaker without experiencing an unpleasant echo.",
                modifier = Modifier.padding(30.dp), textAlign = TextAlign.Center)
            Button(onClick = ***REMOVED***
                if (permissionsState.allPermissionsGranted) ***REMOVED***
                    navController.navigate(SyncStageScreen.Profile.name)
                ***REMOVED*** else ***REMOVED***
                    permissionsState.launchMultiplePermissionRequest()
                ***REMOVED***
            ***REMOVED***) ***REMOVED***
                val title =
                    if (permissionsState.allPermissionsGranted) "NEXT" else "ALLOW ACCESS"
                Text(text = title)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***