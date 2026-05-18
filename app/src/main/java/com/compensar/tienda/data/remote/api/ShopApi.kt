package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.ShopDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ShopApi {

    @GET("api/shop")
    suspend fun getAll(): List<ShopDto>

    @GET("api/shop/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ShopDto

    @GET("api/shop/seller/{idSeller}")
    suspend fun getBySeller(
        @Path("idSeller") idSeller: Long
    ): List<ShopDto>

    @POST("api/shop")
    suspend fun insert(
        @Body shop: ShopDto
    ): ShopDto

    @PUT("api/shop/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body shop: ShopDto
    ): ShopDto

    @DELETE("api/shop/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}