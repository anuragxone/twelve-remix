/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.lineageos.twelve.models.Audio
import org.lineageos.twelve.models.RequestStatus
import org.lineageos.twelve.models.UniqueItem
import org.lineageos.twelve.utils.MimeUtils
import kotlin.reflect.safeCast

class AlbumViewModel(application: Application) : TwelveViewModel(application) {
    private val albumUri = MutableStateFlow<Uri?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val album = albumUri
        .filterNotNull()
        .flatMapLatest {
            mediaRepository.album(it)
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            RequestStatus.Loading()
        )

    sealed interface AlbumContent : UniqueItem<AlbumContent> {
        data class DiscHeader(val discNumber: Int) : AlbumContent {
            override fun areItemsTheSame(other: AlbumContent) =
                DiscHeader::class.safeCast(other)?.let {
                    discNumber == it.discNumber
                } ?: false

            override fun areContentsTheSame(other: AlbumContent) = true
        }

        class AudioItem(val audio: Audio) : AlbumContent {
            override fun areItemsTheSame(other: AlbumContent) = AudioItem::class.safeCast(
                other
            )?.let {
                audio.areItemsTheSame(it.audio)
            } ?: false

            override fun areContentsTheSame(other: AlbumContent) = AudioItem::class.safeCast(
                other
            )?.let {
                audio.areContentsTheSame(it.audio)
            } ?: false
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val albumContent = album
        .mapLatest {
            when (it) {
                is RequestStatus.Loading -> null

                is RequestStatus.Success -> {
                    val discToTracks = it.data.second.groupBy { audio ->
                        audio.discNumber
                    }

                    val hideHeaders = with(discToTracks.keys) {
                        size == 1 && firstOrNull() == 1
                    }

                    mutableListOf<AlbumContent>().apply {
                        discToTracks.keys.sortedBy { disc ->
                            disc ?: 0
                        }.forEach { discNumber ->
                            discNumber?.takeUnless { hideHeaders }?.let { i ->
                                add(AlbumContent.DiscHeader(i))
                            }

                            discToTracks[discNumber]?.let { tracks ->
                                addAll(
                                    tracks.map { audio ->
                                        AlbumContent.AudioItem(audio)
                                    }
                                )
                            }
                        }
                    }.toList()
                }

                is RequestStatus.Error -> listOf()
            }
        }
        .filterNotNull()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            listOf()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val albumFileTypes = album
        .filterNotNull()
        .mapLatest {
            when (it) {
                is RequestStatus.Loading -> null

                is RequestStatus.Success -> {
                    it.data.second
                        .map { audio -> audio.mimeType }
                        .distinct()
                        .takeIf { mimeTypes -> mimeTypes.size <= 2 }
                        ?.mapNotNull { mimeType -> MimeUtils.mimeTypeToDisplayName(mimeType) }
                        .orEmpty()
                }

                is RequestStatus.Error -> listOf()
            }
        }
        .filterNotNull()
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            listOf()
        )

    fun loadAlbum(albumUri: Uri) {
        this.albumUri.value = albumUri
    }

    fun playAlbum(startFrom: Audio? = null) {
        albumContent.value.mapNotNull {
            (it as? AlbumContent.AudioItem)?.audio
        }.takeUnless { it.isEmpty() }?.let { audios ->
            playAudio(audios, startFrom?.let { audios.indexOf(it) } ?: 0)
        }
    }
}
