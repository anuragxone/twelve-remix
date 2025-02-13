/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.models

import android.net.Uri
import androidx.media3.common.MediaMetadata
import org.lineageos.twelve.ext.buildMediaItem
import org.lineageos.twelve.ext.toByteArray

/**
 * A user-defined playlist.
 *
 * @param name The name of the playlist
 */
data class Playlist(
    override val uri: Uri,
    override val thumbnail: Thumbnail?,
    val name: String,
) : MediaItem<Playlist> {
    override val mediaType = MediaType.PLAYLIST

    override fun areContentsTheSame(other: Playlist) = compareValuesBy(
        this, other,
        Playlist::thumbnail,
        Playlist::name,
    ) == 0

    override fun toMedia3MediaItem() = buildMediaItem(
        title = name,
        mediaId = uri.toString(),
        isPlayable = false,
        isBrowsable = true,
        mediaType = MediaMetadata.MEDIA_TYPE_PLAYLIST,
        sourceUri = uri,
        artworkData = thumbnail?.bitmap?.toByteArray(),
        artworkType = thumbnail?.type?.media3Value,
        artworkUri = thumbnail?.uri,
    )
}
