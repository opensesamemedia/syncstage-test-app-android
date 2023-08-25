package media.opensesame.syncstagetestappandroid.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import media.opensesame.syncstagetestappandroid.SyncStageScreen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneAccessScreen(navController: NavHostController) {
    val permissions = mutableListOf(
        android.Manifest.permission.RECORD_AUDIO,
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.ACCESS_WIFI_STATE,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
    }

    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)

    // We allow no location permission
    val allRequiredPermissionsGranted = permissionsState.revokedPermissions.isEmpty() || (permissionsState.revokedPermissions.size == 1 && permissionsState.revokedPermissions[0].permission == android.Manifest.permission.ACCESS_COARSE_LOCATION)

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
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = "Microphone",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = "Please enable phone call management access so you can adjust the volume level in the session and use a speaker without experiencing an unpleasant echo.",
                modifier = Modifier.padding(30.dp), textAlign = TextAlign.Center
            )
            Button(modifier = Modifier.testTag("next_allow_access_btn"),
                onClick = {
                if (allRequiredPermissionsGranted) {
                    navController.navigate(SyncStageScreen.Profile.name)
                } else {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }) {
                val title =
                    if (allRequiredPermissionsGranted) "NEXT" else "ALLOW ACCESS"
                Text(text = title)
            }
        }
    }
}