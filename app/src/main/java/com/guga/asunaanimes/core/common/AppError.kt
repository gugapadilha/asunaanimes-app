package com.guga.asunaanimes.core.common

/**
 * Application wide error taxonomy. Every layer below the presentation layer reports failures
 * through this type so screens never have to reason about exceptions or transport details.
 */
sealed interface AppError {

    val cause: Throwable?

    data class Network(override val cause: Throwable? = null) : AppError

    data class Http(val code: Int, override val cause: Throwable? = null) : AppError

    data class Serialization(override val cause: Throwable? = null) : AppError

    data class Storage(override val cause: Throwable? = null) : AppError

    data class Unknown(override val cause: Throwable? = null) : AppError
}
