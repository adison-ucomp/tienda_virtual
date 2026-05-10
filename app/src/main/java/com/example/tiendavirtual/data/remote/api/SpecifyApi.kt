package com.example.tiendavirtual.data.remote.api

import com.example.tiendavirtual.data.remote.dto.SpecifyDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SpecifyApi {

    @GET("api/specifications")
    suspend fun getAll(): List<SpecifyDto>

    @GET("api/specifications/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): SpecifyDto

    @GET("api/specifications/product/{idProduct}")
    suspend fun getByProduct(
        @Path("idProduct") idProduct: Long
    ): List<SpecifyDto>

    @POST("api/specifications")
    suspend fun insert(
        @Body specify: SpecifyDto
    ): SpecifyDto

    @PUT("api/specifications/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body specify: SpecifyDto
    ): SpecifyDto

    @DELETE("api/specifications/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}