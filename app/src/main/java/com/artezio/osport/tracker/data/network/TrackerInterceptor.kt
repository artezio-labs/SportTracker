package com.artezio.osport.tracker.data.network

import okhttp3.Interceptor
import okhttp3.Response

class TrackerInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        // сюда в урлу можно будет аксесс токен, апи-ключ или ещё что
        val url = request.url().newBuilder().build()
        request = request.newBuilder().url(url).build()
        return chain.proceed(request)
    }
}