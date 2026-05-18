package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.SellerDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface SellerApi {

    @GET("api/sellers")
    suspend fun getAll(): List<SellerDto>

    @GET("api/sellers/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): SellerDto

    @GET("api/sellers/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): SellerDto

    @POST("api/sellers")
    suspend fun insert(
        @Body seller: SellerDto
    ): SellerDto

    @PUT("api/sellers/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body seller: SellerDto
    ): SellerDto

    @DELETE("api/sellers/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}