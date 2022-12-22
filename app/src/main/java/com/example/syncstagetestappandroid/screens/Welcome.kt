package com.example.syncstagetestappandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.syncstagetestappandroid.SyncStageScreen

@Composable
fun Welcome(navController: NavHostController, userName: String?) ***REMOVED***

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) ***REMOVED***
        Column(horizontalAlignment = Alignment.CenterHorizontally) ***REMOVED***
            val name = userName ?: "User"
            Text("Welcome, $name", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.size(30.dp))

            Button(onClick = ***REMOVED***
                navController.navigate(SyncStageScreen.Profile.name) ***REMOVED***
                    popUpTo(SyncStageScreen.Profile.name)
                ***REMOVED***
            ***REMOVED***) ***REMOVED***
                Text(text = "Set up your Profile")
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***