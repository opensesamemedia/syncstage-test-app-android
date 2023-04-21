package media.opensesame.syncstagetestappandroid.screens

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.widget.Toast
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
): ViewModel(), SyncStageUserDelegate, SyncStageConnectivityDelegate ***REMOVED***
    private val _uiState = MutableStateFlow(SessionUIState())
    val uiState: StateFlow<SessionUIState> = _uiState.asStateFlow()
    lateinit var sessionLeft: () -> Unit

    private val connectivityManager by lazy ***REMOVED*** context.get()?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager ***REMOVED***
    private val telephonyManager by lazy ***REMOVED*** context.get()?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ***REMOVED***
    private lateinit var detection5G: Detection5G
    private var networkType: String = ""

    init ***REMOVED***
        initWidgetsState()
    ***REMOVED***

    private val timer = timer("refresh", period = 5000.toLong(), action = ***REMOVED***
        _uiState.update ***REMOVED***
            it.copy(
                date = Date()
            )
        ***REMOVED***
    ***REMOVED***)

    val isMuted: Boolean
        get() ***REMOVED***
            val connection = uiState.value.connections.firstOrNull()
            return connection?.isMuted ?: false
        ***REMOVED***


    val isDirectMonitorEnabled: Boolean
        get() ***REMOVED***
            return uiState.value.directMonitorEnabled
        ***REMOVED***

    val isInternalMicrophoneEnabled: Boolean
        get() ***REMOVED***
            return uiState.value.internalMicrophoneEnabled
        ***REMOVED***

    val transmitterIdentifier: String
        get() ***REMOVED***
            val identifier = uiState.value.session?.transmitter?.identifier
            return identifier ?: ""
        ***REMOVED***

    private fun initWidgetsState() ***REMOVED***
        _uiState.update ***REMOVED*** sessionUIState ->
            sessionUIState.copy(
                directMonitorVolume = syncStage.getDirectMonitorVolume().toFloat() / 100,
                directMonitorEnabled = syncStage.getDirectMonitorEnabled(),
                internalMicrophoneEnabled = syncStage.getInternalMicEnabled(),
            )
        ***REMOVED***
    ***REMOVED***

    fun initiate5GDetection() ***REMOVED***
        context.get()?.let ***REMOVED***
            detection5G = Detection5G(
                ctx = it,
                connectivityManager = connectivityManager,
                onNetworkTypeChange = ***REMOVED*** networkTypeName ->
                    networkType = networkTypeName
                    _uiState.update ***REMOVED*** sessionUIState ->
                        sessionUIState.copy(
                            networkType = networkTypeName
                        )
                    ***REMOVED***
              ***REMOVED***
                telephonyManager = telephonyManager
            )
            detection5G.startListenNetworkType()
        ***REMOVED***
    ***REMOVED***

    private fun updateSession(value: Session) ***REMOVED***
        val connections: MutableList<ConnectionModel> = mutableListOf()
        value.transmitter?.let ***REMOVED***
            connections.add(ConnectionModel(identifier = it.identifier, userId = it.userId, displayName = it.displayName, isMuted = it.isMuted))
        ***REMOVED***
        value.receivers.forEach ***REMOVED*** receiver ->
            connections.add(ConnectionModel(identifier = receiver.identifier,
                userId = receiver.userId,
                displayName = receiver.displayName,
                isMuted = receiver.isMuted))
        ***REMOVED***
        _uiState.update ***REMOVED*** sessionUIState ->
            sessionUIState.copy(
                connections = connections,
                session = value
            )
        ***REMOVED***
    ***REMOVED***

    private fun updateConnection(identifier: String, update: (ConnectionModel) -> ConnectionModel) ***REMOVED***
        CoroutineScope(Dispatchers.Main).launch ***REMOVED***
            val connections = uiState.value.connections.toMutableList()
            val index = connections.indexOfFirst ***REMOVED*** it.identifier == identifier ***REMOVED***
            if (index != -1) ***REMOVED***
                connections[index] = update(connections[index])
                _uiState.update ***REMOVED*** sessionUIState ->
                    sessionUIState.copy(
                        connections = connections
                    )
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun changeReceiverVolume(identifier: String, value: Float) ***REMOVED***
        val result = syncStage.changeReceiverVolume(identifier = identifier, volume = value.toInt())
        if (result == SyncStageSDKErrorCode.OK) ***REMOVED***
            updateConnection(identifier) ***REMOVED***
                it.copy(volume = value)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun getReceiverVolume(identifier: String): Int ***REMOVED***
        return syncStage.getReceiverVolume(identifier = identifier)
    ***REMOVED***

    private fun getDirectMonitorVolume(): Int ***REMOVED***
        val dmVolume = syncStage.getDirectMonitorVolume()
        _uiState.update ***REMOVED*** sessionUIState ->
            sessionUIState.copy(
                directMonitorVolume = (dmVolume/100).toFloat(),
            )
        ***REMOVED***

        return dmVolume
    ***REMOVED***

    fun changeDirectMonitorVolume(volume: Float) ***REMOVED***
        val result = syncStage.changeDirectMonitorVolume((volume * 100).toInt())
        if (result == SyncStageSDKErrorCode.OK) ***REMOVED***
            _uiState.update ***REMOVED*** sessionUIState ->
                sessionUIState.copy(
                    directMonitorVolume = volume,
                )
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun toggleDirectMonitor(value: Boolean) ***REMOVED***
        val result = syncStage.toggleDirectMonitor(value)
        if (result == SyncStageSDKErrorCode.OK) ***REMOVED***
            _uiState.update ***REMOVED***
                it.copy(
                    directMonitorEnabled = value
                )
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun toggleInternalMicrophone(value: Boolean) ***REMOVED***
        val result = syncStage.toggleInternalMic(value)
        if (result == SyncStageSDKErrorCode.OK) ***REMOVED***
            _uiState.update ***REMOVED***
                it.copy(
                    internalMicrophoneEnabled = value
                )
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun joinSession(sessionCode: String) ***REMOVED***
        syncStage.userDelegate = this
        syncStage.connectivityDelegate = this
        val displayName = preferencesRepo.getUserName()
        val userId = preferencesRepo.getUserId()
        CoroutineScope(Dispatchers.IO).launch ***REMOVED***
            val result = syncStage.join(sessionCode = sessionCode, userId = userId, displayName = displayName)
            if(result.second == SyncStageSDKErrorCode.OK) ***REMOVED***
                context.get()?.let ***REMOVED***
                    sendCommandToService(ACTION_START_SERVICE, it)
                ***REMOVED***
                val session = result.first
                session?.let ***REMOVED***
                    CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                        updateSession(it)
                    ***REMOVED***
                ***REMOVED***
                getDirectMonitorVolume()
            ***REMOVED*** else ***REMOVED***
                CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                    context.get()?.let ***REMOVED***
                        Toast.makeText(it, "Could not join the session. Please check the session code.", Toast.LENGTH_LONG).show()
                    ***REMOVED***
                    sessionOut()
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun toggleMicrophone(value: Boolean) ***REMOVED***
        val result = syncStage.toggleMicrophone(value)
        if (result == SyncStageSDKErrorCode.OK) ***REMOVED***
            updateConnection(transmitterIdentifier) ***REMOVED*** connection ->
                connection.copy(isMuted = value)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun leaveSession() ***REMOVED***
        timer.cancel()
        CoroutineScope(Dispatchers.IO).launch ***REMOVED***
            context.get()?.let ***REMOVED***
                sendCommandToService(ACTION_STOP_SERVICE, it)
            ***REMOVED***
            val result = syncStage.leave()
            if(result == SyncStageSDKErrorCode.OK) ***REMOVED***
                CoroutineScope(Dispatchers.Main).launch ***REMOVED***
                    sessionLeft()
                ***REMOVED***
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***

    fun getMeasurements(identifier: String): Measurements ***REMOVED***
        return if(identifier == transmitterIdentifier) ***REMOVED***
            syncStage.getTransmitterMeasurements()
        ***REMOVED*** else ***REMOVED***
            syncStage.getReceiverMeasurements(identifier = identifier)
        ***REMOVED***
    ***REMOVED***

    override fun sessionOut() ***REMOVED***
        sessionLeft()
    ***REMOVED***

    override fun userJoined(connection: Connection) ***REMOVED***
        _uiState.update ***REMOVED***
            val connections = it.connections.toMutableList()
            connections.add(ConnectionModel(identifier = connection.identifier, userId = connection.userId, displayName = connection.displayName, isMuted = connection.isMuted))
            it.copy(
                connections = connections
            )
        ***REMOVED***
    ***REMOVED***

    override fun userLeft(identifier: String) ***REMOVED***
        _uiState.update ***REMOVED*** sessionUIState ->
            val connections = sessionUIState.connections.toMutableList()
            connections.removeIf ***REMOVED*** connection ->
                connection.identifier == identifier
            ***REMOVED***
            sessionUIState.copy(
                connections = connections
            )
        ***REMOVED***
    ***REMOVED***

    override fun userMuted(identifier: String) ***REMOVED***
        updateConnection(identifier) ***REMOVED*** connection ->
            connection.copy(isMuted = true)
        ***REMOVED***
    ***REMOVED***

    override fun userUnmuted(identifier: String) ***REMOVED***
        updateConnection(identifier) ***REMOVED*** connection ->
            connection.copy(isMuted = false)
        ***REMOVED***
    ***REMOVED***

    override fun receiverConnectivityChanged(identifier: String, connected: Boolean) ***REMOVED***
        updateConnection(identifier) ***REMOVED*** connection ->
            connection.copy(isConnected = connected)
        ***REMOVED***
    ***REMOVED***

    override fun transmitterConnectivityChanged(connected: Boolean) ***REMOVED***
        if(transmitterIdentifier == _uiState.value.connections.firstOrNull()?.identifier) ***REMOVED***
            updateConnection(transmitterIdentifier) ***REMOVED***
                it.copy(isConnected = connected)
            ***REMOVED***
        ***REMOVED***
    ***REMOVED***
***REMOVED***
