package media.opensesame.syncstagetestappandroid

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import media.opensesame.syncstagetestappandroid.R
import media.opensesame.syncstagetestappandroid.screens.*
import media.opensesame.syncstagetestappandroid.ui.theme.SyncStageTestAppAndroidTheme
import dagger.hilt.android.AndroidEntryPoint

enum class SyncStageScreen(@StringRes val title: Int) {
    Intro(title = R.string.intro),
    Access(title = R.string.access),
    Profile(title = R.string.profile),
    CreateJoinSession(title = R.string.create_Join_Session),
    Location(title = R.string.location),
    Session(title = R.string.session),
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SyncStageTestAppAndroidTheme {
                SyncStageApp()
            }
        }
    }

    override fun onDestroy() {
        sendCommandToService(ACTION_STOP_SERVICE, this)
        super.onDestroy()
    }
}

@Composable
fun SyncStageAppBar(
    currentScreen: SyncStageScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(canNavigateBack) {
        TopAppBar(
            title = { Text(stringResource(currentScreen.title), modifier = Modifier.fillMaxWidth()) },
            modifier = modifier.fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        )
    } else {
        TopAppBar(
            title = { Text(stringResource(currentScreen.title), modifier = Modifier.fillMaxWidth()) },
            modifier = modifier.fillMaxWidth()
        )
    }
}

class CustomNavController(context: Context) : NavController(context) {
    override fun popBackStack(): Boolean {
        return super.popBackStack()
    }
}

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
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Scaffold(
            //topBar = { TopAppBar(title = { Text(modifier = Modifier.fillMaxWidth(), text = "SyncStage", textAlign = TextAlign.Center) }) },
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
                //val uiState by viewModel.uiState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = SyncStageScreen.Intro.name,
                    modifier = modifier.padding(innerPadding)
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
        )
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SyncStageTestAppAndroidTheme {

    }
}