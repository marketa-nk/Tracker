package com.mint.minttracker.network

import com.mint.minttracker.data.QueryInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkService {

    val googleMapsApi: GoogleMapsApi = getApi(GoogleMapsApi::class.java, "https://maps.googleapis.com/maps/api/geocode/", QueryInterceptor("key", "AIzaSyBuw5zpFxq1U6EpNlwGk8gPEHEB92XGTZA"))

    private fun <T> getApi(clazz: Class<T>, url: String, vararg interceptors: Interceptor): T {

        val httpClient = OkHttpClient.Builder()
            .also { builder ->
                interceptors.forEach {
                    builder.addInterceptor(it)
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.create(clazz)
    }

    companion object {
        var instance: NetworkService = NetworkService()
    }
}