package com.guga.asunaanimes.data.remote

import com.google.gson.JsonParseException
import com.guga.asunaanimes.core.common.AppError
import com.guga.asunaanimes.core.common.AppResult
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

/**
 * Runs a network call and translates every known failure into an [AppError].
 * Cancellation is deliberately rethrown so structured concurrency keeps working.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> = try {
    AppResult.Success(block())
} catch (cancellation: CancellationException) {
    throw cancellation
} catch (httpException: HttpException) {
    AppResult.Failure(AppError.Http(httpException.code(), httpException))
} catch (parseException: JsonParseException) {
    AppResult.Failure(AppError.Serialization(parseException))
} catch (ioException: IOException) {
    AppResult.Failure(AppError.Network(ioException))
} catch (throwable: Throwable) {
    AppResult.Failure(AppError.Unknown(throwable))
}
