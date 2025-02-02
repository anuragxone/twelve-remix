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
import org.lineageos.twelve.models.RequestStatus

class ActivityViewModel(application: Application) : TwelveViewModel(application) {
    val activity = mediaRepository.activity()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            RequestStatus.Loading(),
        )


    private val _uiState = MutableStateFlow(ActivityComposeState(textFieldValue = ""))
    // The UI collects from this StateFlow to get its state updates
    val uiState: StateFlow<ActivityComposeState> = _uiState.asStateFlow()

    fun setTextFieldText(textFieldValue: String){
        _uiState.update {
            currentState ->
            currentState.copy(
                textFieldValue = textFieldValue
            )
        }
    }

}


data class ActivityComposeState(
    var textFieldValue: String
)
