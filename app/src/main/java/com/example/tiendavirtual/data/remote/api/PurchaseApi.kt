package com.example.tiendavirtual.data.remote.api

import com.example.tiendavirtual.data.remote.dto.PurchaseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PurchaseApi {

    @GET("api/purchases")
    suspend fun getAll(): List<PurchaseDto>

    @GET("api/purchases/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): PurchaseDto

    @GET("api/purchases/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): List<PurchaseDto>

    @GET("api/purchases/product/{idProduct}")
    suspend fun getByProduct(
        @Path("idProduct") idProduct: Long
    ): List<PurchaseDto>

    @GET("api/purchases/date/{date}")
    suspend fun getByDate(
        @Path("date") date: String
    ): List<PurchaseDto>

    @POST("api/purchases")
    suspend fun insert(
        @Body purchase: PurchaseDto
    ): PurchaseDto

    @PUT("api/purchases/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body purchase: PurchaseDto
    ): PurchaseDto

    @DELETE("api/purchases/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}