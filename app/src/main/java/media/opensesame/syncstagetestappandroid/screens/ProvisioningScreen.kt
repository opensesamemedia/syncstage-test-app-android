package media.opensesame.syncstagetestappandroid.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import media.opensesame.syncstagetestappandroid.QrCodeAnalyzer
import media.opensesame.syncstagetestappandroid.R
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

    var qrCodeReadOpened by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCamPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        if(qrCodeReadOpened && hasCamPermission){
            Column(modifier = Modifier.fillMaxSize()){
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(
                                Size(
                                    previewView.width,
                                    previewView.height
                                )
                            )
                            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                onProvisioningCodeChange(result)
                                qrCodeReadOpened = false
                            }
                        )
                        try {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        previewView
                    },
                )
            }
        }
        if(!qrCodeReadOpened){
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



                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
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
                            .weight(1f)

                    )

                    IconButton(
                        onClick = {
                            if(!hasCamPermission){
                                provisioningViewModel.displayToast("No camera permission")
                                launcher.launch(Manifest.permission.CAMERA)
                            }else{
                                qrCodeReadOpened = !qrCodeReadOpened
                            }
                        }) {
                        Icon(
                            imageVector = Icons.Filled.QrCode,
                            contentDescription = stringResource(R.string.qr_button)

                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

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

