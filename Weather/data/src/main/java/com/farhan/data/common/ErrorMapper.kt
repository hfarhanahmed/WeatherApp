package com.farhan.data.common

import androidx.room.EmptyResultSetException
import com.farhan.domain.common.Mapper
import com.farhan.domain.common.WError
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorMapper @Inject constructor() : Mapper<Throwable, WError>() {

    override fun mapFrom(from: Throwable): WError {
        return when (from) {
            is UnknownHostException -> WError.Offline(from)
            is SocketTimeoutException -> WError.Timeout(from)
            is EmptyResultSetException -> WError.NoWeatherInDatabase(from)
            else -> WError.Unknown(from)
        }
    }
}