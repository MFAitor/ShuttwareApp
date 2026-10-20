package com.example.shuttwareapp.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body datos: Map<String, String>): Response<Map<String, Any>>

    @POST("auth/registro")
    suspend fun registro(@Body datos: Map<String, String>): Response<Map<String, Any>>
}