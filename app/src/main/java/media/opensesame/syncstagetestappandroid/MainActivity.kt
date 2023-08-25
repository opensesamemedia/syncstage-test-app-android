package media.opensesame.syncstagetestappandroid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.largeTopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagetestappandroid.screens.*
import media.opensesame.syncstagetestappandroid.ui.theme.AppTheme
import javax.inject.Inject

enum class SyncStageScreen(@StringRes val title: Int) {
    Intro(title = R.string.intro),
    Access(title = R.string.access),
    Profile(title = R.string.profile),
    CreateJoinSession(title = R.string.create_Join_Session),
    Location(title = R.string.location),
    LocationLatencies(title = R.string.location),
    LocationManual(title = R.string.location),
    Session(title = R.string.session),
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var syncStage: SyncStage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = true, dynamicColor = false) {
                SyncStageApp()
            }
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            syncStage.stop()
            sendCommandToService(ACTION_STOP_SERVICE, this)
        }
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncStageAppBar(
    currentScreen: SyncStageScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {

    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.ss_logo_text),
                contentDescription = "",
                modifier = Modifier.height(40.dp),

                )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        colors = largeTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        )
    )

}

class CustomNavController(context: Context) : NavController(context)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SyncStageApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = SyncStageScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("?") ?: SyncStageScreen.Intro.name
    )
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                SyncStageAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null
                            && navController.currentDestination?.route?.substringBefore("?") != SyncStageScreen.Session.name,
                    navigateUp = {
                        navController.navigateUp()
                    }
                )
            },
            content = { innerPadding ->
                val boxSize = with(LocalDensity.current) { 300.dp.toPx() }
                //val uiState by viewModel.uiState.collectAsState()
                Box(
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                MaterialTheme.colorScheme.inverseOnSurface
                            ),
                            start = Offset(boxSize / 2, 0.4f),
                            end = Offset(0f, boxSize)
                        )
                    )
                )
                {
                    NavHost(
                        navController = navController,
                        startDestination = SyncStageScreen.Intro.name,
                        modifier = modifier.padding(innerPadding)
                            .semantics {
                                testTagsAsResourceId = true
                            }
                    ) {
                        composable(route = SyncStageScreen.Intro.name) {
                            IntroScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.Access.name) {
                            MicrophoneAccessScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.Profile.name) {
                            ProfileScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.CreateJoinSession.name) {
                            CreateJoinSessionScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.Location.name) {
                            LocationScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.LocationManual.name) {
                            LocationManualScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.LocationLatencies.name) {
                            LocationLatenciesScreen(navController = navController)
                        }

                        composable(route = SyncStageScreen.Session.name + "?sessionCode={sessionCode}") {
                            val sessionCode = it.arguments?.getString("sessionCode")
                            sessionCode?.let { sessionCode ->
                                SessionScreen(navController = navController, sessionCode)
                            }
                        }

                        /*composable(NavRoutes.Welcome.route + "/?{userName}") {
                            val userName = it.arguments?.getString("userName")
                            Welcome(navController = navController, userName)
                        }

                        composable(NavRoutes.Profile.route) {
                            Profile()
                        }*/
                    }

                }

            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AppTheme {

    }
}