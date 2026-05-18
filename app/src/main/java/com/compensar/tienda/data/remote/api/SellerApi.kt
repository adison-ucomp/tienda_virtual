package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.SellerDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SellerApi {

    @GET("api/seller")
    suspend fun getAll(): List<SellerDto>

    @GET("api/seller/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): SellerDto

    @GET("api/seller/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): SellerDto

    @POST("api/seller")
    suspend fun insert(
        @Body seller: SellerDto
    ): SellerDto

    @PUT("api/seller/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body seller: SellerDto
    ): SellerDto

    @DELETE("api/seller/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}