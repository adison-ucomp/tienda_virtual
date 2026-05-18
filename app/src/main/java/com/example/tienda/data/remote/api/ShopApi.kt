package com.example.tienda.data.remote.api

import com.example.tienda.data.remote.dto.ShopDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShopApi {

    @GET("api/shops")
    suspend fun getAll(): List<ShopDto>

    @GET("api/shops/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ShopDto

    @GET("api/shops/seller/{idSeller}")
    suspend fun getBySeller(
        @Path("idSeller") idSeller: Long
    ): List<ShopDto>

    @POST("api/shops")
    suspend fun insert(
        @Body shop: ShopDto
    ): ShopDto

    @PUT("api/shops/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body shop: ShopDto
    ): ShopDto

    @DELETE("api/shops/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}