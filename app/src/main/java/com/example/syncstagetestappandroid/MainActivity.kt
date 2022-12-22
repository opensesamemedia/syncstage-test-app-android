package com.example.syncstagetestappandroid

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.syncstagetestappandroid.screens.*
import com.example.syncstagetestappandroid.ui.theme.SyncStageTestAppAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.delegates.SyncStageUserDelegate

enum class SyncStageScreen(@StringRes val title: Int) ***REMOVED***
    Intro(title = R.string.intro),
    Access(title = R.string.access),
    Profile(title = R.string.profile),
    CreateJoinSession(title = R.string.create_Join_Session),
    Location(title = R.string.location),
    Session(title = R.string.session),
***REMOVED***

@AndroidEntryPoint
class MainActivity : ComponentActivity() ***REMOVED***
    override fun onCreate(savedInstanceState: Bundle?) ***REMOVED***
        super.onCreate(savedInstanceState)
        setContent ***REMOVED***
            SyncStageTestAppAndroidTheme ***REMOVED***
                SyncStageApp()
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***

@Composable
fun SyncStageAppBar(
    currentScreen: SyncStageScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) ***REMOVED***
    if(canNavigateBack) ***REMOVED***
        TopAppBar(
            title = ***REMOVED*** Text(stringResource(currentScreen.title), modifier = Modifier.fillMaxWidth()) ***REMOVED***,
            modifier = modifier.fillMaxWidth(),
            navigationIcon = ***REMOVED***
                IconButton(onClick = navigateUp) ***REMOVED***
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                ***REMOVED***
            ***REMOVED***
        )
    ***REMOVED*** else ***REMOVED***
        TopAppBar(
            title = ***REMOVED*** Text(stringResource(currentScreen.title), modifier = Modifier.fillMaxWidth()) ***REMOVED***,
            modifier = modifier.fillMaxWidth()
        )
    ***REMOVED***
***REMOVED***

class CustomNavController(context: Context) : NavController(context) ***REMOVED***
    override fun popBackStack(): Boolean ***REMOVED***
        return super.popBackStack()
    ***REMOVED***
***REMOVED***

@Composable
fun SyncStageApp(
    modifier: Modifier = Modifier
) ***REMOVED***
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = SyncStageScreen.valueOf(
        backStackEntry?.destination?.route?.substringBefore("?") ?: SyncStageScreen.Intro.name
    )
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) ***REMOVED***
        Scaffold(
            //topBar = ***REMOVED*** TopAppBar(title = ***REMOVED*** Text(modifier = Modifier.fillMaxWidth(), text = "SyncStage", textAlign = TextAlign.Center) ***REMOVED***) ***REMOVED***,
            topBar = ***REMOVED***
                SyncStageAppBar(
                    currentScreen = currentScreen,
                    canNavigateBack = navController.previousBackStackEntry != null
                            && navController.currentDestination?.route?.substringBefore("?") != SyncStageScreen.Session.name,
                    navigateUp = ***REMOVED***
                        navController.navigateUp()
                    ***REMOVED***
                )
          ***REMOVED***
            content = ***REMOVED*** innerPadding ->
                //val uiState by viewModel.uiState.collectAsState()

                NavHost(
                    navController = navController,
                    startDestination = SyncStageScreen.Intro.name,
                    modifier = modifier.padding(innerPadding)
                ) ***REMOVED***
                    composable(route = SyncStageScreen.Intro.name) ***REMOVED***
                        IntroScreen(navController = navController)
                    ***REMOVED***

                    composable(route = SyncStageScreen.Access.name) ***REMOVED***
                        MicrophoneAccessScreen(navController = navController)
                    ***REMOVED***

                    composable(route = SyncStageScreen.Profile.name) ***REMOVED***
                        ProfileScreen(navController = navController)
                    ***REMOVED***

                    composable(route = SyncStageScreen.CreateJoinSession.name) ***REMOVED***
                        CreateJoinSessionScreen(navController = navController)
                    ***REMOVED***

                    composable(route = SyncStageScreen.Location.name) ***REMOVED***
                        LocationScreen(navController = navController)
                    ***REMOVED***

                    composable(route = SyncStageScreen.Session.name + "?sessionCode=***REMOVED***sessionCode***REMOVED***") ***REMOVED***
                        val sessionCode = it.arguments?.getString("sessionCode")
                        sessionCode?.let ***REMOVED*** sessionCode ->
                            SessionScreen(navController = navController, sessionCode)
                        ***REMOVED***
                    ***REMOVED***

                    /*composable(NavRoutes.Welcome.route + "/?***REMOVED***userName***REMOVED***") ***REMOVED***
                        val userName = it.arguments?.getString("userName")
                        Welcome(navController = navController, userName)
                    ***REMOVED***

                    composable(NavRoutes.Profile.route) ***REMOVED***
                        Profile()
                    ***REMOVED****/
                ***REMOVED***
            ***REMOVED***
        )
    ***REMOVED***
***REMOVED***



@Preview(showBackground = true)
@Composable
fun DefaultPreview() ***REMOVED***
    SyncStageTestAppAndroidTheme ***REMOVED***

    ***REMOVED***
***REMOVED***