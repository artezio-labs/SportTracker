package com.artezio.sporttracker.di

import com.artezio.sporttracker.data.network.ApiService
import com.artezio.sporttracker.data.network.TrackerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesBaseUrl() {
        // TODO: 19.01.2022 Нужно будет запровайдить базовую урлу
    }

    @Provides
    fun providesOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(TrackerInterceptor())
        .build()

    @Provides
    fun providesRetrofit(baseUrl: String, client: OkHttpClient) =
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()

    @Provides
    fun providesApiService(retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)
}