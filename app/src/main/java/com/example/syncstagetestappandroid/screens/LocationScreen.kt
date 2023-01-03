package com.example.syncstagetestappandroid.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.syncstagetestappandroid.SyncStageScreen
import com.example.syncstagetestappandroid.components.LoadingIndicator

@Composable
fun LocationScreen(navController: NavHostController, zoneViewModel: LocationViewModel = hiltViewModel()) ***REMOVED***
    val zoneUIState by zoneViewModel.uiState.collectAsState()

    var mTextFieldSize by remember ***REMOVED*** mutableStateOf(Size.Zero) ***REMOVED***
    var mExpanded by remember ***REMOVED*** mutableStateOf(false) ***REMOVED***
    var showLoadingIndicator by remember ***REMOVED*** mutableStateOf(false) ***REMOVED***

    zoneViewModel.createSessionCallback = ***REMOVED*** sessionCode ->
        showLoadingIndicator = false
        navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$sessionCode")
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
                    .clickable ***REMOVED***
                        mExpanded = !mExpanded
                    ***REMOVED***
                    .fillMaxWidth()
                    .border(width = 2.dp, color = Color.Blue, shape = RoundedCornerShape(5.dp))
                    .height(60.dp)
                    .onGloballyPositioned ***REMOVED***
                        mTextFieldSize = it.size.toSize()
                    ***REMOVED***
            ) ***REMOVED***
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) ***REMOVED***
                    Text(zoneUIState.selectedZone.ZoneName, modifier = Modifier.padding(start = 10.dp))
                    Spacer(modifier = Modifier.weight(1.0f))
                    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
                    Icon(
                        icon, "contentDescription",
                        Modifier.padding(end = 10.dp).size(30.dp, 30.dp)
                    )
                ***REMOVED***
                DropdownMenu(
                    expanded = mExpanded,
                    onDismissRequest = ***REMOVED*** mExpanded = false ***REMOVED***,
                    modifier = Modifier
                        .width(with(LocalDensity.current) ***REMOVED***
                            mTextFieldSize.width.toDp()
                        ***REMOVED***)
                        .fillMaxHeight(0.3f)
                ) ***REMOVED***
                    zoneUIState.zones.forEach ***REMOVED*** zone ->
                        DropdownMenuItem(onClick = ***REMOVED***
                            zoneViewModel.updateSelectedZone(zone)
                            mExpanded = false
                        ***REMOVED***) ***REMOVED***
                            Text(text = zone.ZoneName)
                        ***REMOVED***
                    ***REMOVED***
                ***REMOVED***
            ***REMOVED***
            Button(onClick = ***REMOVED***
                showLoadingIndicator = true
                zoneViewModel.createNewSession()
                //navController.navigate(SyncStageScreen.Session.name)
          ***REMOVED*** modifier = Modifier.padding(top = 20.dp), enabled = zoneUIState.selectedZone.zoneId.isNotEmpty()) ***REMOVED***
                Text(text = "START NOW")
            ***REMOVED***
        ***REMOVED***
        if (zoneUIState.zones.isEmpty() || showLoadingIndicator) ***REMOVED***
            LoadingIndicator()
        ***REMOVED***
    ***REMOVED***

    LaunchedEffect(Unit) ***REMOVED***
        if (zoneUIState.zones.isEmpty()) ***REMOVED***
            zoneViewModel.getZones()
        ***REMOVED***
    ***REMOVED***
***REMOVED***
