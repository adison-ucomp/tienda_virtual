package com.example.tiendavirtual.data.remote.api

import com.example.tiendavirtual.data.remote.dto.RoleDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoleApi {

    @GET("api/roles")
    suspend fun getAll(): List<RoleDto>

    @GET("api/roles/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): RoleDto

    @POST("api/roles")
    suspend fun insert(
        @Body role: RoleDto
    ): RoleDto

    @PUT("api/roles/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body role: RoleDto
    ): RoleDto

    @DELETE("api/roles/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}