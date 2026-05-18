package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.GatewayDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GatewayApi {

    @GET("api/gateway")
    suspend fun getAll(): List<GatewayDto>

    @GET("api/gateway/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): GatewayDto

    @POST("api/gateway")
    suspend fun insert(
        @Body gateway: GatewayDto
    ): GatewayDto

    @PUT("api/gateway/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body gateway: GatewayDto
    ): GatewayDto

    @DELETE("api/gateway/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}