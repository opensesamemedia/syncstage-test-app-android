package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import media.opensesame.networkutils.Detection5G
import media.opensesame.syncstagesdk.SyncStage
import media.opensesame.syncstagesdk.SyncStageSDKErrorCode
import media.opensesame.syncstagesdk.delegates.SyncStageConnectivityDelegate
import media.opensesame.syncstagesdk.delegates.SyncStageUserDelegate
import media.opensesame.syncstagesdk.models.public.Connection
import media.opensesame.syncstagesdk.models.public.Measurements
import media.opensesame.syncstagesdk.models.public.Session
import media.opensesame.syncstagetestappandroid.ACTION_START_SERVICE
import media.opensesame.syncstagetestappandroid.ACTION_STOP_SERVICE
import media.opensesame.syncstagetestappandroid.repo.PreferencesRepo
import media.opensesame.syncstagetestappandroid.sendCommandToService
import java.lang.ref.WeakReference
import java.util.*
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
    val connections: MutableList<ConnectionModel> = mutableListOf(),
    val networkType: String = "",
    val date: Date = Date(),
    val directMonitorEnabled: Boolean = false,
    val directMonitorVolume: Float = 1F,
    val internalMicrophoneEnabled: Boolean = false
)


@HiltViewModel
class SessionViewModel @Inject constructor(
    private val context: WeakReference<Context>,
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
    private lateinit var detection5G: Detection5G
    private var networkType: String = ""



    init {
        initWidgetsState()
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
            val connection = uiState.value.connections.firstOrNull()
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

    val transmitterIdentifier: String
        get() {
            val identifier = uiState.value.session?.transmitter?.identifier
            return identifier ?: ""
        }

    private fun initWidgetsState() {
        _uiState.update { sessionUIState ->
            sessionUIState.copy(
                directMonitorVolume = syncStage.getDirectMonitorVolume().toFloat() / 100,
                directMonitorEnabled = syncStage.getDirectMonitorEnabled(),
                internalMicrophoneEnabled = syncStage.getInternalMicEnabled(),
            )
        }
    }

    fun initiate5GDetection() {
        context.get()?.let {
            detection5G = Detection5G(
                ctx = it,
                connectivityManager = connectivityManager,
                onNetworkTypeChange = { networkTypeName ->
                    networkType = networkTypeName
                    _uiState.update { sessionUIState ->
                        sessionUIState.copy(
                            networkType = networkTypeName
                        )
                    }
                },
                telephonyManager = telephonyManager
            )
            detection5G.startListenNetworkType()
        }
    }

    private fun updateSession(value: Session) {
        val connections: MutableList<ConnectionModel> = mutableListOf()
        value.transmitter?.let {
            connections.add(
                ConnectionModel(
                    identifier = it.identifier,
                    userId = it.userId,
                    displayName = it.displayName,
                    isMuted = it.isMuted
                )
            )
        }
        value.receivers.forEach { receiver ->
            connections.add(
                ConnectionModel(
                    identifier = receiver.identifier,
                    userId = receiver.userId,
                    displayName = receiver.displayName,
                    isMuted = receiver.isMuted
                )
            )
        }
        _uiState.update { sessionUIState ->
            sessionUIState.copy(
                connections = connections,
                session = value
            )
        }
    }

    private fun updateConnection(identifier: String, update: (ConnectionModel) -> ConnectionModel) {
        CoroutineScope(Dispatchers.Main).launch {
            val connections = uiState.value.connections.toMutableList()
            val index = connections.indexOfFirst { it.identifier == identifier }
            if (index != -1) {
                connections[index] = update(connections[index])
                _uiState.update { sessionUIState ->
                    sessionUIState.copy(
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
        _uiState.update { sessionUIState ->
            sessionUIState.copy(
                directMonitorVolume = (dmVolume / 100).toFloat(),
            )
        }

        return dmVolume
    }

    fun changeDirectMonitorVolume(volume: Float) {
        val result = syncStage.changeDirectMonitorVolume((volume * 100).toInt())
        if (result == SyncStageSDKErrorCode.OK) {
            _uiState.update { sessionUIState ->
                sessionUIState.copy(
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
                displayName = displayName
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

    fun toggleMicrophone(value: Boolean) {
        val result = syncStage.toggleMicrophone(value)
        if (result == SyncStageSDKErrorCode.OK) {
            updateConnection(transmitterIdentifier) { connection ->
                connection.copy(isMuted = value)
            }
        }
    }

    fun leaveSession() {
        timer.cancel()
        CoroutineScope(Dispatchers.IO).launch {

            val result = syncStage.leave()
            if (result == SyncStageSDKErrorCode.OK) {
                CoroutineScope(Dispatchers.Main).launch {
                    sessionLeft()
                }
            }
        }
    }

    fun getMeasurements(identifier: String): Measurements {
        return if (identifier == transmitterIdentifier) {
            syncStage.getTransmitterMeasurements()
        } else {
            syncStage.getReceiverMeasurements(identifier = identifier)
        }
    }

    fun startForegroundService(){
        context.get()?.let {
            sendCommandToService(ACTION_START_SERVICE, it)
        }
    }

    fun stopForegroundService(){
        context.get()?.let {
            sendCommandToService(ACTION_STOP_SERVICE, it)
        }
    }

    override fun sessionOut() {
        sessionLeft()
    }

    override fun userJoined(connection: Connection) {
        _uiState.update {
            val connections = it.connections.toMutableList()
            connections.add(
                ConnectionModel(
                    identifier = connection.identifier,
                    userId = connection.userId,
                    displayName = connection.displayName,
                    isMuted = connection.isMuted
                )
            )
            it.copy(
                connections = connections
            )
        }
    }

    override fun userLeft(identifier: String) {
        _uiState.update { sessionUIState ->
            val connections = sessionUIState.connections.toMutableList()
            connections.removeIf { connection ->
                connection.identifier == identifier
            }
            sessionUIState.copy(
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
        if (transmitterIdentifier == _uiState.value.connections.firstOrNull()?.identifier) {
            updateConnection(transmitterIdentifier) {
                it.copy(isConnected = connected)
            }
        }
    }
}
