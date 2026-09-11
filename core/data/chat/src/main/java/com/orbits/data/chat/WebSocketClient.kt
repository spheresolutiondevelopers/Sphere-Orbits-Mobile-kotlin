package com.orbits.data.chat

import com.orbits.core.common.Logger
import com.orbits.data.chat.remote.WebSocketMessage
import com.orbits.data.chat.remote.WebSocketMessagePayload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WebSocketClient @Inject constructor() {

    companion object {
        private const val TAG = "WebSocketClient"
    }

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    private var webSocket: WebSocket? = null

    /**
     * Connect to the WebSocket server.
     */
    suspend fun connect(
        accessToken: String,
        userId: String
    ): Flow<WebSocketMessage> = callbackFlow {
        val request = Request.Builder()
            .url("wss://api.sphereschedule.pro/ws?userId=$userId")
            .header("Authorization", "Bearer $accessToken")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Logger.d(TAG, "WebSocket connected")
                this@WebSocketClient.webSocket = webSocket
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = json.decodeFromString(WebSocketMessage.serializer(), text)
                    trySend(message)
                } catch (e: Exception) {
                    Logger.e(TAG, "Error parsing WebSocket message", e)
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Not used for our text-based protocol
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Logger.d(TAG, "WebSocket closing: $code - $reason")
                webSocket.close(code, reason)
                close()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Logger.d(TAG, "WebSocket closed: $code - $reason")
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Logger.e(TAG, "WebSocket failure", t)
                close(t)
            }
        }

        val ws = okHttpClient.newWebSocket(request, listener)
        this@WebSocketClient.webSocket = ws

        awaitClose {
            Logger.d(TAG, "Closing WebSocket")
            ws.close(1000, "Closed by client")
        }
    }

    /**
     * Send a message through the WebSocket.
     */
    suspend fun sendMessage(message: WebSocketMessage): Boolean {
        return withContext(Dispatchers.IO) {
            val ws = webSocket
            if (ws == null) {
                Logger.w(TAG, "WebSocket not connected")
                return@withContext false
            }

            try {
                val jsonString = json.encodeToString(WebSocketMessage.serializer(), message)
                ws.send(jsonString)
                true
            } catch (e: Exception) {
                Logger.e(TAG, "Error sending WebSocket message", e)
                false
            }
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    fun disconnect() {
        webSocket?.close(1000, "Disconnected by client")
        webSocket = null
        Logger.d(TAG, "WebSocket disconnected")
    }

    /**
     * Check if the WebSocket is connected.
     */
    fun isConnected(): Boolean {
        return webSocket != null
    }
}