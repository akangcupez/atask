package com.akangcupez.atask.utils

sealed class Resource<T>(val data: T? = null, val error: String? = null) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Empty<T>(data: T? = null) : Resource<T>(data)
    class Completed<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(error: String, data: T? = null) : Resource<T>(data, error)
}
