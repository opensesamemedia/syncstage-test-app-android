package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyDisplayInfo
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import media.opensesame.syncstagesdk.delegates.SyncStageConnectivityDelegate
import media.opensesame.syncstagesdk.delegates.SyncStageUserDelegate
import media.opensesame.syncstagesdk.models.public.Connection
import media.opensesame.syncstagesdk.models.public.Measurements
import media.opensesame.syncstagesdk.models.public.Session
import media.opensesame.syncstagesdk.utils.getNetworkTypeOldAPI
import media.opensesame.syncstagetestappandroid.ACTION_START_SERVICE
import media.opensesame.syncstagetestappandroid.ACTION_STOP_SERVICE
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import media.opensesame.syncstagetestappandroid.sendCommandToService
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.concurrent.timer

data class ConnectionModel(
    val identifier: String = "",
    val userId: String = "",
    val displayName: String? = "",
    var isMuted: Boolean = false,
    var isConnected: Boolean = true,
    var volume: Float = 90.0f
)

data class SessionUIState(
    val session: Session? = null,
    val transmitterConnection: ConnectionModel? = null,
    val connections: HashMap<String, ConnectionModel> = HashMap(),
    val networkTypeOldApi: String = "",
    val date: Date = Date(),
    val directMonitorEnabled: Boolean = false,
    val directMonitorVolume: Float = 1F,
    val internalMicrophoneEnabled: Boolean = false,
    val isRecording: Boolean = false,
    val recordingRequestPending: Boolean = false,
)


@HiltViewModel
class SessionViewModel @Inject constructor(
    val context: WeakReference<Context>,
    private val syncStage: SyncStage,
    private val preferencesRepo: PreferencesRepo
) : ViewModel(), SyncStageUserDelegate, SyncStageConnectivityDelegate {
    private val _uiState = MutableStateFlow(SessionUIState())
    val uiState: StateFlow<SessionUIState> = _uiState.asStateFlow()
    lateinit var sessionLeft: () -> Unit

    private val connectivityManager by lazy {
        context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val telephonyManager by lazy {
        context.get()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
    private var networkTypeOldApiJob: Job? = null

    private fun startNetworkTypeOldApiJob() {
        if (networkTypeOldApiJob == null && Build.VERSION.SDK_INT < 30) {
            networkTypeOldApiJob = viewModelScope.launch(Dispatchers.IO) {
                while (true) {
                    Log.d("SessionViewModel", "Running networkTypeOldApiJob")
                    context.get()?.let {
                        _uiState.update { sessionUIState ->
                            sessionUIState.copy(
                                networkTypeOldApi = getNetworkTypeOldAPI(it)
                            )
                        }
                    }
                    delay(2000)
                }
            }
        }
    }

    // Based on https://github.com/tdcolvin/NetworkTypeDetector
    val telephonyType = callbackFlow {
        // The thread Executor used to run the listener. This governs how threads are created and
        // reused. Here we use a single thread.
        val exec = Executors.newSingleThreadExecutor()

        if (Build.VERSION.SDK_INT >= 31) {
            // SDK >= 31 uses TelephonyManager.registerTelephonyCallback() to listen for
            // TelephonyDisplayInfo changes.
            // It does not require any permissions.

            val callback = object : TelephonyCallback(), TelephonyCallback.DisplayInfoListener {
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    trySend(telephonyDisplayInfo)
                }
            }
            telephonyManager.registerTelephonyCallback(exec, callback)

            awaitClose {
                telephonyManager.unregisterTelephonyCallback(callback)
                exec.shutdown()
            }
        } else {
            // SDK 30 uses TelephonyManager.listen() to listen for TelephonyDisplayInfo changes.
            // It requires READ_PHONE_STATE permission.

            @Suppress("OVERRIDE_DEPRECATION")
            val callback = object : PhoneStateListener(exec) {
                override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
                    trySend(telephonyDisplayInfo)
                }
            }
            telephonyManager.listen(callback, PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED)

            awaitClose {
                telephonyManager.listen(callback, 0)
                exec.shutdown()
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)


    init {
        initWidgetsState()
        startNetworkTypeOldApiJob()
    }

    private val timer = timer("refresh", period = 5000.toLong(), action = {
        _uiState.update {
            it.copy(
                date = Date()
            )
        }
    })

    val isMuted: Boolean
        get() {
            val connection = uiState.value.transmitterConnection
            return connection?.isMuted ?: false
        }


    val isDirectMonitorEnabled: Boolean
        get() {
            return uiState.value.directMonitorEnabled
        }

    val isInternalMicrophoneEnabled: Boolean
        get() {
            return uiState.value.internalMicrophoneEnabled
        }

    private fun initWidgetsState() {
        _uiState.update {
            it.copy(
                directMonitorVolume = syncStage.getDirectMonitorVolume().toFloat() / 100,
                directMonitorEnabled = syncStage.getDirectMonitorEnabled(),
                internalMicrophoneEnabled = syncStage.getInternalMicEnabled(),
            )
        }
    }

    private fun updateSession(value: Session) {
        var transmitterConnection: ConnectionModel? = null
        val connections: HashMap<String, ConnectionModel> = HashMap()

        value.transmitter?.let {
            transmitterConnection =
                ConnectionModel(
                    identifier = it.identifier,
                    userId = it.userId,
                    displayName = it.displayName,
                    isMuted = it.isMuted
                )

        }
        value.receivers.forEach { receiver ->
            connections[receiver.identifier] = ConnectionModel(
                identifier = receiver.identifier,
                userId = receiver.userId,
                displayName = receiver.displayName,
                isMuted = receiver.isMuted
            )
        }
        _uiState.update {
            it.copy(
                transmitterConnection = transmitterConnection,
                connections = connections,
                session = value,
                isRecording = value.isRecording
            )
        }
    }

    private fun updateConnection(identifier: String, update: (ConnectionModel) -> ConnectionModel) {
        CoroutineScope(Dispatchers.Main).launch {
            val connections = uiState.value.connections
            val connection = connections[identifier]
            if (connection != null) {
                connections[connection.identifier] = update(connection)
                _uiState.update {
                    it.copy(
                        connections = connections
                    )
                }
            }
        }
    }

    fun changeReceiverVolume(identifier: String, value: Float) {
        val result = syncStage.changeReceiverVolume(identifier = identifier, volume = value.toInt())
        if (result == SyncStageSDKErrorCode.OK) {
            updateConnection(identifier) {
                it.copy(volume = value)
            }
        }
    }

    fun getReceiverVolume(identifier: String): Int {
        return syncStage.getReceiverVolume(identifier = identifier)
    }

    private fun getDirectMonitorVolume(): Int {
        val dmVolume = syncStage.getDirectMonitorVolume()
        _uiState.update {
            it.copy(
                directMonitorVolume = (dmVolume / 100).toFloat(),
            )
        }

        return dmVolume
    }

    fun changeDirectMonitorVolume(volume: Float) {
        val result = syncStage.changeDirectMonitorVolume((volume * 100).toInt())
        if (result == SyncStageSDKErrorCode.OK) {
            _uiState.update {
                it.copy(
                    directMonitorVolume = volume,
                )
            }
        }
    }

    fun toggleDirectMonitor(value: Boolean) {
        val result = syncStage.toggleDirectMonitor(value)
        if (result == SyncStageSDKErrorCode.OK) {
            _uiState.update {
                it.copy(
                    directMonitorEnabled = value
                )
            }
        }
    }

    fun toggleInternalMicrophone(value: Boolean) {
        val result = syncStage.toggleInternalMic(value)
        if (result == SyncStageSDKErrorCode.OK) {
            _uiState.update {
                it.copy(
                    internalMicrophoneEnabled = value
                )
            }
        }
    }

    fun joinSession(sessionCode: String) {
        syncStage.userDelegate = this
        syncStage.connectivityDelegate = this
        val displayName = preferencesRepo.getUserName()
        val userId = preferencesRepo.getUserId()
        CoroutineScope(Dispatchers.IO).launch {
            val result = syncStage.join(
                sessionCode = sessionCode,
                userId = userId,
                displayName = displayName,
                zoneId = preferencesRepo.getZoneId(),
                studioServerId = preferencesRepo.getStudioServerId(),
            )
            if (result.second == SyncStageSDKErrorCode.OK) {
                val session = result.first
                session?.let {
                    CoroutineScope(Dispatchers.Main).launch {
                        updateSession(it)
                    }
                }
                getDirectMonitorVolume()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    context.get()?.let {
                        Toast.makeText(
                            it,
                            "Could not join the session. Please check the session code.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    sessionOut()
                }
            }
        }
    }

    fun startRecording() {
        _uiState.update {
            it.copy(
                recordingRequestPending = true
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val result = syncStage.startRecording()
            _uiState.update {
                it.copy(
                    recordingRequestPending = false
                )
            }
        }
    }

    fun stopRecording() {
        _uiState.update {
            it.copy(
                recordingRequestPending = true
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val result = syncStage.stopRecording()
            _uiState.update {
                it.copy(
                    recordingRequestPending = false
                )
            }
        }
    }

    fun toggleMicrophone(value: Boolean) {
        val result = syncStage.toggleMicrophone(value)
        if (result == SyncStageSDKErrorCode.OK) {
            _uiState.update {
                val transmitterConnection = it.transmitterConnection
                transmitterConnection?.isMuted = value
                it.copy(
                    transmitterConnection = transmitterConnection
                )
            }

        }
    }

    fun leaveSession() {
        timer.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            syncStage.leave()
            sessionLeft()
        }
    }

    fun getMeasurements(identifier: String): Measurements {
        return if (identifier == uiState.value.session?.transmitter?.identifier) {
            syncStage.getTransmitterMeasurements()
        } else {
            syncStage.getReceiverMeasurements(identifier = identifier)
        }
    }

    fun startForegroundService() {
        context.get()?.let {
            sendCommandToService(ACTION_START_SERVICE, it)
        }
    }

    fun stopForegroundService() {
        context.get()?.let {
            sendCommandToService(ACTION_STOP_SERVICE, it)
        }
    }

    override fun sessionOut() {
        sessionLeft()
    }

    override fun sessionRecordingStarted() {
        _uiState.update {
            it.copy(
                isRecording = true
            )
        }
    }

    override fun sessionRecordingStopped() {
        _uiState.update {
            it.copy(
                isRecording = false
            )
        }
    }

    override fun userJoined(connection: Connection) {
        _uiState.update {
            val connections = it.connections
            connections[connection.identifier] = ConnectionModel(
                identifier = connection.identifier,
                userId = connection.userId,
                displayName = connection.displayName,
                isMuted = connection.isMuted
            )
            it.copy(
                connections = connections
            )
        }
    }

    override fun userLeft(identifier: String) {
        _uiState.update {
            val connections = it.connections
            connections.remove(identifier)
            it.copy(
                connections = connections
            )
        }
    }

    override fun userMuted(identifier: String) {
        updateConnection(identifier) { connection ->
            connection.copy(isMuted = true)
        }
    }

    override fun userUnmuted(identifier: String) {
        updateConnection(identifier) { connection ->
            connection.copy(isMuted = false)
        }
    }

    override fun receiverConnectivityChanged(identifier: String, connected: Boolean) {
        updateConnection(identifier) { connection ->
            connection.copy(isConnected = connected)
        }
    }

    override fun transmitterConnectivityChanged(connected: Boolean) {
        _uiState.update {
            val transmitterConnection = it.transmitterConnection
            transmitterConnection?.isConnected = connected
            it.copy(
                transmitterConnection = transmitterConnection
            )
        }
    }
}
