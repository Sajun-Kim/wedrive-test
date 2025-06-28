package com.namuplanet.base.data.handler

import com.namuplanet.base.data.Failure
import com.namuplanet.base.data.remote.HttpResponse
import kotlinx.coroutines.Deferred
import timber.log.Timber
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.namuplanet.base.data.Result
import retrofit2.HttpException

suspend fun <T> Deferred<T>.awaitResult(): Result<T> {
    return when (val response: T = try {
        await()
    } catch (e: HttpException) {
        if (e.code() == 401) {
            return Result.Error(Failure.AuthError)
        } else {
            val result = e.response()?.errorBody()?.string()?.toString()?.split(":") ?: listOf()
            val serverMsg = when(result.isNotEmpty()) {
                true  -> result[1].replace("[\"}]".toRegex(), "")
                false -> ""
            }

            return Result.Error(Failure.HttpError(e.code().toString(), e.message(), serverMsg))
        }
    } catch (e: SocketTimeoutException) {
        return Result.Error(Failure.TimeOutError)
    } catch (e: ConnectException) {
        Timber.d(e.toString())
        return Result.Error(Failure.NoConnection)
    } catch (e: UnknownHostException) {
        return Result.Error(Failure.NoConnection)
    } catch (e: IOException) {
        Timber.d(e.toString())
        return Result.Error(Failure.Exception(Throwable(e.message)))
    } catch (t: Throwable) {
        return Result.Error(Failure.Exception(t))
    }) {
        is HttpResponse -> {
            if (response.onHandled()) Result.HandleResult()
            else if (response.isSuccessful()) Result.Success<T>(response)
            else Result.Error(response.apiError ?: Failure.NoConnection)
        }
        else -> {
            Result.Success(response)
        }
    }
}