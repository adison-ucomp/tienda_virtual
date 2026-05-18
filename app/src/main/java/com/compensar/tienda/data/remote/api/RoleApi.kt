package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.RoleDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoleApi {

    @GET("api/role")
    suspend fun getAll(): List<RoleDto>

    @GET("api/role/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): RoleDto

    @POST("api/role")
    suspend fun insert(
        @Body role: RoleDto
    ): RoleDto

    @PUT("api/role/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body role: RoleDto
    ): RoleDto

    @DELETE("api/role/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}