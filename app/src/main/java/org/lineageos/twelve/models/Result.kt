/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.twelve.models

/**
 * Result status. This is very similar to Arrow's `Either<A, B>`
 */
sealed class Result<T, E> {
    /**
     * The result is ready.
     *
     * @param data The obtained data
     */
    class Success<T, E>(val data: T) : Result<T, E>()

    /**
     * The request failed.
     *
     * @param error The error
     */
    class Error<T, E>(val error: E, val throwable: Throwable? = null) : Result<T, E>()

    companion object {
        /**
         * Map the result to another type.
         */
        fun <T, E, R> Result<T, E>.map(
            mapping: (T) -> R
        ): Result<R, E> = when (this) {
            is Success -> Success(mapping(data))
            is Error -> Error(error, throwable)
        }

        /**
         * Map the result to another type.
         */
        @JvmName("mapNullable")
        fun <T, E, R> Result<T, E>?.map(
            mapping: (T) -> R
        ): Result<R, E>? = this?.map(mapping)

        /**
         * Fold the request status.
         */
        fun <T, E, R> Result<T, E>.fold(
            onSuccess: (T) -> R,
            onError: (E) -> R,
        ): R = when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(error)
        }

        /**
         * Fold the request status.
         */
        fun <T, E, R> Result<T, E>?.fold(
            onNull: () -> R,
            onSuccess: (T) -> R,
            onError: (E) -> R,
        ): R = this?.fold(onSuccess, onError) ?: onNull()
    }
}
