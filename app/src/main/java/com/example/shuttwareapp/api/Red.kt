package com.example.shuttwareapp.api

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import kotlin.jvm.java

object Red {

    //Conexion con la IP donde alojo la API

    //Para probar por id de wifi
    private const val BASE_URL = "http://172.20.10.3:8080/"

    //Para probar con ngrok
    //private const val BASE_URL = "https://retreat-disrupt-recoup.ngrok-free.dev/"

    //Cliente que gestiona las peticiones
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    @RequiresApi(Build.VERSION_CODES.O)
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            LocalDate::class.java,
            JsonDeserializer { json, _, _ ->
                LocalDate.parse(json.asString)
            }
        )
        .create()

    //Retrofit se comunica con la API
    @RequiresApi(Build.VERSION_CODES.O)
    val api: ApiService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(ApiService::class.java)
}