package media.opensesame.syncstagetestappandroid.screens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import media.opensesame.syncstagesdk.SyncStage
import javax.inject.Inject
import media.opensesame.syncstagetestappandroid.BuildConfig

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val syncStage: SyncStage
): ViewModel() ***REMOVED***
   fun getAppVersion(): String***REMOVED***
       return BuildConfig.VERSION_NAME
   ***REMOVED***
***REMOVED***