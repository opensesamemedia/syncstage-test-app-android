package media.opensesame.syncstagetestappandroid.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import media.opensesame.syncstagetestappandroid.R
import media.opensesame.syncstagetestappandroid.SyncStageScreen


@Composable
fun IntroScreen(navController: NavHostController, introViewModel: IntroViewModel = hiltViewModel()) ***REMOVED***
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) ***REMOVED***
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()) ***REMOVED***
            Image(painter = painterResource(id = R.drawable.syncstage), contentDescription = "", modifier = Modifier.size(120.dp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "SyncStage Example Application", fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 17.sp))
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = " $***REMOVED***introViewModel.getAppVersion()***REMOVED***", fontWeight = FontWeight.Bold, style = TextStyle(fontSize = 17.sp))
            Text(
                buildAnnotatedString ***REMOVED***
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) ***REMOVED***
                        append("SyncStage")
                    ***REMOVED***
                    append(" is a patent-pending voice chat platform that allows you to sing, jam, learn, win together with audio latency lower ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) ***REMOVED***
                        append("than ever before.")
                    ***REMOVED***
              ***REMOVED***
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center
            )

            Button(onClick = ***REMOVED***
                navController.navigate(SyncStageScreen.Access.name)
            ***REMOVED***) ***REMOVED***
                Text(text = "START")
            ***REMOVED***

        ***REMOVED***

    ***REMOVED***
***REMOVED***