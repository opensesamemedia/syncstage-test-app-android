package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.SyncStageScreen
import media.opensesame.syncstagetestappandroid.components.LoadingIndicator

@Composable
fun LocationScreen(
    navController: NavHostController,
    zoneViewModel: LocationViewModel = hiltViewModel()
) {
    val zoneUIState by zoneViewModel.uiState.collectAsState()

    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
    var mExpanded by remember { mutableStateOf(false) }
    var showLoadingIndicator by remember { mutableStateOf(false) }

    zoneViewModel.createSessionCallback = { sessionCode ->
        showLoadingIndicator = false
        navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$sessionCode")
    }

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
                .padding(30.dp)
                .padding(bottom = 50.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Session Location",
                textAlign = TextAlign.Left,
                style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Select the closest location for all session participants.",
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            )
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable {
                        mExpanded = !mExpanded
                    }
                    .fillMaxWidth()
                    .border(width = 2.dp, color = Color.Blue, shape = RoundedCornerShape(5.dp))
                    .height(60.dp)
                    .onGloballyPositioned {
                        mTextFieldSize = it.size.toSize()
                    }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        zoneUIState.selectedZone.ZoneName,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    val icon =
                        if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                    Icon(
                        icon, "contentDescription",
                        Modifier
                            .padding(end = 10.dp)
                            .size(30.dp, 30.dp)
                    )
                }
                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = { mExpanded = false },
                    modifier = Modifier
                        .width(with(LocalDensity.current) {
                            mTextFieldSize.width.toDp()
                        })
                        .fillMaxHeight(0.3f)
                ) {
                    zoneUIState.zones.forEach { zone ->
                        DropdownMenuItem(onClick = {
                            zoneViewModel.updateSelectedZone(zone)
                            mExpanded = false
                        }) {
                            Text(text = zone.ZoneName)
                        }
                    }
                }
            }
            Button(
                onClick = {
                    showLoadingIndicator = true
                    zoneViewModel.createNewSession()
                    //navController.navigate(SyncStageScreen.Session.name)
                },
                modifier = Modifier.padding(top = 20.dp),
                enabled = zoneUIState.selectedZone.zoneId.isNotEmpty()
            ) {
                Text(text = "START NOW")
            }
        }
        if (zoneUIState.zones.isEmpty() || showLoadingIndicator) {
            LoadingIndicator()
        }
    }

    LaunchedEffect(Unit) {
        if (zoneUIState.zones.isEmpty()) {
            zoneViewModel.getZones()
        }
    }
}
