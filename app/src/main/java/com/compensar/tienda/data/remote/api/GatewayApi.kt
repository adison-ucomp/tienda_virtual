package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.GatewayDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GatewayApi {

    @GET("api/gateways")
    suspend fun getAll(): List<GatewayDto>

    @GET("api/gateways/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): GatewayDto

    @POST("api/gateways")
    suspend fun insert(
        @Body gateway: GatewayDto
    ): GatewayDto

    @PUT("api/gateways/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body gateway: GatewayDto
    ): GatewayDto

    @DELETE("api/gateways/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}