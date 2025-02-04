/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.currentCompositionErrors
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
import org.lineageos.twelve.ext.applicationContext
import org.lineageos.twelve.models.RequestStatus
import org.lineageos.twelve.repositories.innertube.Player
import org.lineageos.twelve.utils.ktorclient.KtorClient
import org.lineageos.twelve.workers.JavascriptWorker
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class ActivityViewModel(application: Application) : TwelveViewModel(application) {
    val activity = mediaRepository.activity()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            RequestStatus.Loading(),
        )

    private val _uiState =
        MutableStateFlow(ActivityComposeState("", "", "", "", "", "", "", "", "", ""))
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
//            if (signatureCipher != null) {
//                Log.d("video information", signatureCipher)
//            }
            val url = URLDecoder.decode(signatureCipher, StandardCharsets.UTF_8.toString())
            val sParam = Regex("s=(.*)&sp=").find(url)?.groupValues?.get(1)
            val audioUrl = Regex("url=(.*)").find(url)?.groupValues?.get(1)
            val decodedAudioUrl = URLDecoder.decode(audioUrl, StandardCharsets.UTF_8.toString())
            val decodedAudioUri = Uri.parse(decodedAudioUrl)
            val nParam = decodedAudioUri.getQueryParameter("n")
//            Log.d("audio url", decodedAudioUrl)
//            if (nParam != null) {
//                Log.d("n param", nParam)
//            }
            val finalSigSource = buildWorkmanagerJsSigSource(sParam ?: "")
            val finalNsigSource = buildWorkmanagerJsNSigSource(nParam ?: "")
//            Log.d("final nsig", finalNsigSource)
            sendWorkmanagerRequest(finalSigSource, finalNsigSource)

            _uiState.update { state ->
                state.copy(
                    audioUrl = decodedAudioUrl ?: "audioUrl"
                )

            }
            buildFinalAudioUrl(_uiState.value.audioUrl, _uiState.value.decipheredSig, _uiState.value.decipheredNsig)
            Log.d("final url", _uiState.value.audioUrl)
        }


    }

    private fun buildWorkmanagerJsSigSource(param: String): String {
        val sc = _uiState.value.sigSc
        val scSig = sc + "var sig=\"$param\";descramble_sig(sig)"
        return scSig
    }

    private fun buildWorkmanagerJsNSigSource(param: String): String {
        val sc = _uiState.value.nsigSc
        val scSig = "var nsig=\"$param\";" + sc
        return scSig
    }

    private suspend fun sendWorkmanagerRequest(sigSc: String, nsigSc: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val sigData = Data.Builder().putString("sc", sigSc).build()
        val nsigData = Data.Builder().putString("sc", nsigSc).build()
        val sigWorkRequest =
            OneTimeWorkRequestBuilder<JavascriptWorker>().setInputData(sigData).build()
        val nSigWorkRequest =
            OneTimeWorkRequestBuilder<JavascriptWorker>().setInputData(nsigData).build()
        workManager.beginWith(sigWorkRequest).then(nSigWorkRequest).enqueue()
        val workInfoSig = workManager.getWorkInfoByIdFlow(sigWorkRequest.id)
            .first { it?.state == WorkInfo.State.SUCCEEDED }
        val workInfoNSig = workManager.getWorkInfoByIdFlow(nSigWorkRequest.id)
            .first { it?.state == WorkInfo.State.SUCCEEDED }
        val sigOutputData = workInfoSig?.outputData?.getString("sc_output")
        val nSigOutputData = workInfoNSig?.outputData?.getString("sc_output")

//        Log.d("WorkManagerSig", "Work completed with output: $sigOutputData")
//        Log.d("WorkManagerNsig", "Work completed with output: $nSigOutputData")
        _uiState.update { currentState ->
            currentState.copy(
                decipheredSig = sigOutputData ?: "",
                decipheredNsig = nSigOutputData ?: ""
            )
        }
    }

    fun buildFinalAudioUrl(audioUrl: String, sig: String, nSig: String){
        val pot = innertubeClient.pot
        val baseUri = Uri.parse(audioUrl)
//        val newUri = baseUri.buildUpon()
//            .appendQueryParameter("sig", sig)
//            .appendQueryParameter("n", nSig)
//            .appendQueryParameter("pot", pot)
//            .build()
        val builder = baseUri.buildUpon().clearQuery()
        for (key in baseUri.queryParameterNames) {
            if (key != "n") {
                for (value in baseUri.getQueryParameters(key)) {
                    builder.appendQueryParameter(key, value)
                }
            }
        }
        builder.appendQueryParameter("n", nSig)
        builder.appendQueryParameter("sig", sig)
        builder.appendQueryParameter("pot", pot)

        val finalUrl = builder.build().toString()
        _uiState.update {
            state ->
            state.copy(audioUrl=finalUrl)
        }

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
    var audioUrl: String,
    var decipheredSig: String,
    var decipheredNsig: String
)
