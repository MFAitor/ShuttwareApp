package com.example.shuttwareapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object Red {

    //Conexion con la IP donde alojo la API

    //Para probar por id de wifi
    private const val BASE_URL = "http://192.168.68.61:8080/"

    //Para probar con ngrok
    //private const val BASE_URL = "https://retreat-disrupt-recoup.ngrok-free.dev/"

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