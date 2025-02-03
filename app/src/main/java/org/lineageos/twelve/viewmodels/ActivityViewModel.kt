/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.lineageos.twelve.datasources.innertube.InnertubeClient
import org.lineageos.twelve.models.RequestStatus
import org.lineageos.twelve.repositories.innertube.Player
import org.lineageos.twelve.utils.ktorclient.KtorClient

class ActivityViewModel(application: Application) : TwelveViewModel(application) {
    val activity = mediaRepository.activity()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            RequestStatus.Loading(),
        )

    private val _uiState = MutableStateFlow(ActivityComposeState("", "", "", "", "", "", "", "hi"))
    val uiState: StateFlow<ActivityComposeState> = _uiState.asStateFlow()

    private val innertubeClient = InnertubeClient(KtorClient)
    private val player = Player(innertubeClient)

    fun setTextFieldText(textFieldValue: String) {
        _uiState.update { currentState ->
            currentState.copy(
                textFieldValue = textFieldValue
            )
        }
    }

    fun setVideoId(videoId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                videoId = videoId
            )
        }
    }

    fun getBaseJs() {
        viewModelScope.launch {
            val baseJs = innertubeClient.getBaseJs()
            _uiState.update { currentState ->
                currentState.copy(
                    baseJs = baseJs
                )
            }
            Log.d("basejs", _uiState.value.baseJs)
        }
    }

    fun getSigSc() {
        _uiState.update { state ->
            state.copy(
                sigSc = player.extractSigSourceCode(_uiState.value.baseJs)
            )
        }
        Log.d("sig_sc", _uiState.value.sigSc)
    }

    fun getNsigSc() {
        _uiState.update { state ->
            state.copy(
                nsigSc = player.getNsigSource(_uiState.value.baseJs)
            )
        }
        Log.d("nsig_sc", _uiState.value.nsigSc)
    }

    suspend fun getVideoInfo(videoId: String): String? {

        val info: String = innertubeClient.getInfo(videoId)
//            Log.d("video_info", info)
        val jsonElement: JsonElement = Json.parseToJsonElement(info)
        val jsonObject = jsonElement.jsonObject
        val streamingData = jsonObject["streamingData"]?.jsonObject
        val adaptiveFormats = streamingData?.get("adaptiveFormats")?.jsonArray
        val format = adaptiveFormats?.get(adaptiveFormats.size - 1)?.jsonObject
        val signatureCipher = format?.get("signatureCipher")?.jsonPrimitive?.content
        return signatureCipher


    }

    fun descrambleUrl(videoId: String) {
        viewModelScope.launch {
            val signatureCipher = getVideoInfo(videoId)
            val uri = Uri.parse(signatureCipher)
            val sParam = uri.getQueryParameter("s")
            val audioUrl = uri.getQueryParameter("url")
            _uiState.update { state ->
                state.copy(
                    audioUrl = audioUrl ?: "audioUrl"
                )
            }

        }
        Log.d("audioUrl", _uiState.value.audioUrl)

    }


}

data class ActivityComposeState(
    var textFieldValue: String,
    var videoId: String,
    var baseJs: String,
    var videoInfo: String,
    var sigSc: String,
    var nsigSc: String,
    var url: String,
    var audioUrl: String
)
