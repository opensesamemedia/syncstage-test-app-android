package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun LocationLatenciesScreen(
    navController: NavHostController,
    zoneViewModel: LocationLatenciesViewModel = hiltViewModel()
) {
//    val zoneUIState by zoneViewModel.uiState.collectAsState()
//
//    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
//    var mExpanded by remember { mutableStateOf(false) }
//    var showLoadingIndicator by remember { mutableStateOf(false) }
//
//    zoneViewModel.createSessionCallback = { sessionCode ->
//        showLoadingIndicator = false
//        navController.navigate(route = SyncStageScreen.Session.name + "?sessionCode=$sessionCode")
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState()),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .padding(30.dp)
//                .padding(bottom = 50.dp)
//        ) {
//            Text(
//                modifier = Modifier.fillMaxWidth(),
//                text = "Session Location",
//                textAlign = TextAlign.Left,
//                style = MaterialTheme.typography.titleLarge
//            )
//            Text(
//                text = "Select the closest location for all session participants.",
//                textAlign = TextAlign.Left,
//                modifier = Modifier
//                    .padding(bottom = 20.dp)
//                    .fillMaxWidth()
//            )
//            Box(contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .clickable {
//                        mExpanded = !mExpanded
//                    }
//                    .fillMaxWidth()
//                    .border(
//                        width = 2.dp,
//                        color = MaterialTheme.colorScheme.primary,
//                        shape = RoundedCornerShape(5.dp)
//                    )
//                    .height(60.dp)
//                    .onGloballyPositioned {
//                        mTextFieldSize = it.size.toSize()
//                    }
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Text(
//                        zoneUIState.selectedZone.ZoneName,
//                        modifier = Modifier.padding(start = 10.dp)
//                    )
//                    Spacer(modifier = Modifier.weight(1.0f))
//                    val icon =
//                        if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown
//                    Icon(
//                        icon, "contentDescription",
//                        Modifier
//                            .padding(end = 10.dp)
//                            .size(30.dp, 30.dp)
//                    )
//                }
//                DropdownMenu(
//                    expanded = mExpanded,
//                    onDismissRequest = { mExpanded = false },
//                    modifier = Modifier
//                        .width(with(LocalDensity.current) {
//                            mTextFieldSize.width.toDp()
//                        })
//                        .fillMaxHeight(0.3f)
//                ) {
//                    zoneUIState.zones.forEach { zone ->
//                        DropdownMenuItem(onClick = {
//                            zoneViewModel.updateSelectedZone(zone)
//                            mExpanded = false
//                        }) {
//                            Text(
//                                text = zone.ZoneName,
//                                color = MaterialTheme.colorScheme.inverseOnSurface
//                            )
//                        }
//                    }
//                }
//            }
//            Button(
//                onClick = {
//                    showLoadingIndicator = true
//                    zoneViewModel.createNewSession()
//                    //navController.navigate(SyncStageScreen.Session.name)
//                },
//                modifier = Modifier.padding(top = 20.dp),
//                enabled = zoneUIState.selectedZone.zoneId.isNotEmpty()
//            ) {
//                Text(text = "START NOW")
//            }
//        }
//        if (zoneUIState.zones.isEmpty() || showLoadingIndicator) {
//            LoadingIndicator()
//        }
//    }
//
//    LaunchedEffect(Unit) {
//        if (zoneUIState.zones.isEmpty()) {
//            zoneViewModel.getZones()
//        }
//    }
}
