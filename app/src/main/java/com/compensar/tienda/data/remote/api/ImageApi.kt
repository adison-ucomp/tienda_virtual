package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.ImageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ImageApi {

    @GET("api/image")
    suspend fun getAll(): List<ImageDto>

    @GET("api/image/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ImageDto

    @GET("api/image/product/{idProduct}")
    suspend fun getByProduct(
        @Path("idProduct") idProduct: Long
    ): List<ImageDto>

    @POST("api/image")
    suspend fun insert(
        @Body image: ImageDto
    ): ImageDto

    @PUT("api/image/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body image: ImageDto
    ): ImageDto

    @DELETE("api/image/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}