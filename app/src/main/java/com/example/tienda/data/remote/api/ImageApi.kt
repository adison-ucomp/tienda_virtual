package com.example.tienda.data.remote.api

import com.example.tienda.data.remote.dto.ImageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ImageApi {

    @GET("api/images")
    suspend fun getAll(): List<ImageDto>

    @GET("api/images/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ImageDto

    @GET("api/images/product/{idProduct}")
    suspend fun getByProduct(
        @Path("idProduct") idProduct: Long
    ): List<ImageDto>

    @POST("api/images")
    suspend fun insert(
        @Body image: ImageDto
    ): ImageDto

    @PUT("api/images/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body image: ImageDto
    ): ImageDto

    @DELETE("api/images/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}