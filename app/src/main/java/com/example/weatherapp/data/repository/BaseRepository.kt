package com.example.weatherapp.data.repository

import android.util.Log
import com.example.weatherapp.data.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T//
    ): Resource<T> {
        return withContext(Dispatchers.IO) {

            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                Log.e("Error Error Error ♥", throwable.toString())
                when (throwable) {
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }
}