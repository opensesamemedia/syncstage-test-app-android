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
fun Welcome(navController: NavHostController, userName: String?) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val name = userName ?: "User"
            Text("Welcome, $name", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.size(30.dp))

            Button(onClick = {
                navController.navigate(SyncStageScreen.Profile.name) {
                    popUpTo(SyncStageScreen.Profile.name)
                }
            }) {
                Text(text = "Set up your Profile")
            }
        }
    }
}