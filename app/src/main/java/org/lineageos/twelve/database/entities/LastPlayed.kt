/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.database.entities

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Last played audio URI for each data source.
 *
 * @param dataSource The data source
 * @param mediaUri The last played audio URI
 */
@Entity
data class LastPlayed(
    @PrimaryKey @ColumnInfo(name = "data_source") val dataSource: String,
    @ColumnInfo(name = "media_uri") val mediaUri: Uri,
)
