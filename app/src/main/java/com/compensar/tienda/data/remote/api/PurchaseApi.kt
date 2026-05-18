package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.PurchaseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PurchaseApi {

    @GET("api/purchase")
    suspend fun getAll(): List<PurchaseDto>

    @GET("api/purchase/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): PurchaseDto

    @GET("api/purchase/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): List<PurchaseDto>

    @GET("api/purchase/product/{idProduct}")
    suspend fun getByProduct(
        @Path("idProduct") idProduct: Long
    ): List<PurchaseDto>

    @GET("api/purchase/date/{date}")
    suspend fun getByDate(
        @Path("date") date: String
    ): List<PurchaseDto>

    @POST("api/purchase")
    suspend fun insert(
        @Body purchase: PurchaseDto
    ): PurchaseDto

    @PUT("api/purchase/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body purchase: PurchaseDto
    ): PurchaseDto

    @DELETE("api/purchase/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}