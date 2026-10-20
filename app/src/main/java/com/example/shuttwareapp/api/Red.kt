package com.example.shuttwareapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object Red {

    //Conexion con la IP donde alojo la API
    private const val BASE_URL = "http://192.168.1.45:8080/"

    //Cliente que gestiona las peticiones
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    //Retrofit se comunica con la API
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)
}