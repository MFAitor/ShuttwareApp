package com.example.shuttwareapp.api

import com.example.shuttwareapp.model.Hueco
import com.example.shuttwareapp.model.Preview
import com.example.shuttwareapp.model.Sesion
import com.example.shuttwareapp.model.SolicitudSesion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
interface ApiService {

    //Autenticacion del usuario
    @POST("auth/login")
    suspend fun login(@Body datos: Map<String, String>): Response<Map<String, Any>>

    @POST("auth/registro")
    suspend fun registro(@Body datos: Map<String, String>): Response<Map<String, Any>>

    //Operaciones con las sesiones del cliente
    @GET("sesiones")
    suspend fun listarSesiones(
        @Query("idUsuario") idUsuario: Int
    ): List<Sesion>

    @GET("sesiones/con-previews")
    suspend fun sesionesConPreviews(
        @Query("idUsuario") idUsuario: Int
    ): List<Sesion>

    //Operaciones con los datos y el perfil del cliente
    @GET("clientes/{id}")
    suspend fun verCliente(@Path("id") id: Int): Response<Map<String, Any>>

    @PUT("clientes/{id}")
    suspend fun modificarCliente(
        @Path("id") id: Int,
        @Body datos: @JvmSuppressWildcards Map<String, Any>
    ): Response<@JvmSuppressWildcards Map<String, Any>>

    @PUT("clientes/{id}/contrasena")
    suspend fun cambiarContrasena(
        @Path("id") id: Int,
        @Body datos: Map<String, String>
    ): Response<Map<String, Any>>

    //Carga de huecos libres
    @GET("huecos/libres")
    suspend fun obtenerHuecosLibres(): List<Hueco>

    //Solicitar sesion en hueco libre
    @POST("sesiones")
    suspend fun solicitarSesion(@Body datos: SolicitudSesion): Response<Map<String, Any>>

    //Carga de las previews de una sesion
    @GET("sesiones/{id}/previews")
    suspend fun listarPreviews(
        @Path("id") idSesion: Int,
        @Query("idUsuario") idUsuario: Int
    ): Response<Map<String, Any>>

    //Actualizar el tipo de entrega de una sesion
    //Se usa @JvmSuppressWildcards porque Retrofit + Map generico da problemas de tipos en Kotlin
    @PUT("sesiones/{id}")
    suspend fun actualizarSesion(
        @Path("id") id: Int,
        @Body body: @JvmSuppressWildcards Map<String, Any>
    ): Response<@JvmSuppressWildcards Map<String, Any>>

    //Carga de huecos libres, recibiendo un id para comparar desde la ultima vez que se notifico
    @GET("huecos/libres")
    suspend fun obtenerHuecosLibres(
        @Query("desdeId") desdeId: Int? = null
    ): List<Hueco>

    //Averigua si hay nuevas previews subidas
    @GET("previews/nuevas")
    suspend fun obtenerPreviewsNuevas(
        @Query("idUsuario") idUsuario: Int,
        @Query("desdeId") desdeId: Int = 0
    ): List<Preview>
}