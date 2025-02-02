/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.viewmodels

import android.app.Application
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

    private val _uiState = MutableStateFlow(ActivityComposeState("","","", ""))
    val uiState: StateFlow<ActivityComposeState> = _uiState.asStateFlow()

    private val innertubeClient = InnertubeClient(KtorClient)
    private val player = Player(innertubeClient)

    fun setTextFieldText(textFieldValue: String){
        _uiState.update {
            currentState ->
            currentState.copy(
                textFieldValue = textFieldValue
            )
        }
    }

    fun setVideoId(videoId: String){
        _uiState.update { currentState ->
            currentState.copy(
                videoId = videoId
            )
        }
    }

    fun getBaseJs(){
        viewModelScope.launch {
            val baseJs = innertubeClient.getBaseJs()
            _uiState.update { currentState ->
                currentState.copy(
                    baseJs = baseJs
                )
            }
        }
    }

    fun getSigSc(): String {
        return player.extractSigSourceCode(_uiState.value.baseJs)
    }

    fun getNsigSc(): String {
        return player.getNsigSource(_uiState.value.baseJs)
    }


}

data class ActivityComposeState(
    var textFieldValue: String,
    var videoId: String,
    var baseJs: String,
    var videoInfo: String
)
