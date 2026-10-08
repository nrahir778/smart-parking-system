package com.example.gateway

import android.util.Log
import com.example.data.model.GatewayConfig
import com.example.data.model.ParkingLotState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GatewaySyncManager {

    companion object {
        private const val TAG = "GatewaySync"
        const val DEFAULT_FIREBASE_URL = "https://smart-parking-iot-default-rtdb.firebaseio.com/parking_live.json"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    private val _config = MutableStateFlow(
        GatewayConfig(
            enabled = true,
            endpointUrl = DEFAULT_FIREBASE_URL
        )
    )
    val config: StateFlow<GatewayConfig> = _config.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private var lastSyncedJson = ""

    fun updateEndpoint(url: String) {
        _config.value = _config.value.copy(endpointUrl = url.trim())
    }

    fun toggleGateway(enabled: Boolean) {
        _config.value = _config.value.copy(enabled = enabled)
    }

    fun syncParkingState(state: ParkingLotState, force: Boolean = false) {
        val currentConfig = _config.value
        if (!currentConfig.enabled) return

        val payload = createJsonPayload(state)
        val payloadStr = payload.toString(2)

        // Only send if state changed or forced
        if (!force && payload.toString() == lastSyncedJson) {
            return
        }

        scope.launch {
            try {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = payload.toString().toRequestBody(mediaType)

                val rawUrl = currentConfig.endpointUrl.ifBlank { DEFAULT_FIREBASE_URL }.trim()
                val targetUrl = when {
                    rawUrl.endsWith(".json") -> rawUrl
                    rawUrl.endsWith("/") -> "${rawUrl}parking_live.json"
                    else -> "$rawUrl/parking_live.json"
                }
                val request = Request.Builder()
                    .url(targetUrl)
                    .put(body) // Firebase Realtime DB REST accepts PUT to overwrite root object
                    .build()

                val startTime = System.currentTimeMillis()
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                val latency = System.currentTimeMillis() - startTime

                lastSyncedJson = payload.toString()

                withContext(Dispatchers.Main) {
                    _config.value = _config.value.copy(
                        lastSyncTime = System.currentTimeMillis(),
                        lastStatusCode = response.code,
                        lastStatusMessage = if (response.isSuccessful) {
                            "Sync OK (${latency}ms)"
                        } else {
                            "HTTP ${response.code}: ${response.message}"
                        },
                        totalPacketsSent = _config.value.totalPacketsSent + 1,
                        lastPayloadJson = payloadStr
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gateway sync error: ${e.message}")
                withContext(Dispatchers.Main) {
                    // Update state to record the attempt and show payload
                    _config.value = _config.value.copy(
                        lastSyncTime = System.currentTimeMillis(),
                        lastStatusCode = -1,
                        lastStatusMessage = "Network notice: ${e.localizedMessage ?: "Connection attempt made"}",
                        lastPayloadJson = payloadStr
                    )
                }
            }
        }
    }

    fun createJsonPayload(state: ParkingLotState): JSONObject {
        return JSONObject().apply {
            put("slot1", state.slot1)
            put("slot2", state.slot2)
            put("slot3", state.slot3)
            put("occupiedCount", state.totalOccupied)
            put("totalSlots", state.totalSlots)
            put("availableCount", state.availableCount)
            put("isFull", state.isFull)
            put("gateState", state.gateState.name)
            put("buzzerAlert", state.buzzerAlert)
            put("timestamp", state.lastUpdated)
            put("source", "Android Gateway (HC-05)")
            put("school", "Shree Sarkari Madhyamik Shala Lakhapar")
            put("slots", JSONObject().apply {
                put("s1", JSONObject().apply {
                    put("occupied", state.slot1)
                    put("car", if (state.slot1) "Lime Green Police Cruiser" else "Empty")
                })
                put("s2", JSONObject().apply {
                    put("occupied", state.slot2)
                    put("car", if (state.slot2) "Lime Green Sports GT Coupe" else "Empty")
                })
                put("s3", JSONObject().apply {
                    put("occupied", state.slot3)
                    put("car", if (state.slot3) "Vintage Red Classic Beetle" else "Empty")
                })
            })
        }
    }
}
