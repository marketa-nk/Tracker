package com.mint.minttracker.data

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class QueryInterceptor(private val key: String, private val value: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

        val newUrl: HttpUrl = request.url()
            .newBuilder()
            .addQueryParameter(key, value)
            .build()

        val newRequest = request.newBuilder().url(newUrl).build()
        return chain.proceed(newRequest)
    }
}