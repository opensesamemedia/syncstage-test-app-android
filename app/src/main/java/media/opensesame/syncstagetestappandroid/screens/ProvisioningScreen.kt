package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import media.opensesame.syncstagetestappandroid.SyncStageScreen
import media.opensesame.syncstagetestappandroid.components.LoadingIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvisioningScreen(
    navController: NavHostController,
    provisioningViewModel: ProvisioningViewModel = hiltViewModel(),
) {
    val provisioningUIState by provisioningViewModel.uiState.collectAsState()

    val onProvisioningCodeChange = { text: String ->
        provisioningViewModel.updateProvisioningCode(text)
    }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current
    provisioningViewModel.onSyncStageProvisioned = {
        CoroutineScope(Dispatchers.Main).launch {
        navController.navigate(SyncStageScreen.Access.name)
    }}

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
                .fillMaxHeight()
                .padding(start = 10.dp)
        ) {
            Text(
                text = "Provisioning",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Please enter a 9 digit code or read QR code to connect this installation with your application defined in the Developer Console. \n" +
                        "\n" +
                        "You can find it in Developer Console > Applications -> Configure.",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Left
            )

            OutlinedTextField(
                value = provisioningUIState.provisioningCode,
                singleLine = true,
                onValueChange = onProvisioningCodeChange,
                label = { Text(text = "Provisioning Code") },
                placeholder= { Text(text = "123456789")},
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                ),
                keyboardActions = KeyboardActions(
                    onNext = { // on next.
                        focusManager.clearFocus()
                        provisioningViewModel.sendProvisioningCode()
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .padding(bottom = 10.dp)
                    .testTag("username_input")

            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navController.navigate(SyncStageScreen.HowToGetACode.name)
                }) {
                    Text(text = "Previous")
                }

                Button(modifier = Modifier.testTag("provision_next_btn"),
                    onClick = {
                        provisioningViewModel.sendProvisioningCode()
                    }, enabled = !provisioningUIState.provisioningInProgress) {
                    Text(text = "Provision")
                }
            }

        }
        if (provisioningUIState.provisioningInProgress) {
            LoadingIndicator()
        }

    }
}

