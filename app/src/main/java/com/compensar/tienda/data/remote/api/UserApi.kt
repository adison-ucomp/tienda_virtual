package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("api/users")
    suspend fun getAll(): List<UserDto>

    @GET("api/users/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): UserDto

    @GET("api/users/email/{email}")
    suspend fun getByEmail(
        @Path("email") email: String
    ): UserDto

    @GET("api/users/role/{idRole}")
    suspend fun getByRole(
        @Path("idRole") idRole: Long
    ): List<UserDto>

    @POST("api/users")
    suspend fun insert(
        @Body user: UserDto
    ): UserDto

    @PUT("api/users/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body user: UserDto
    ): UserDto

    @DELETE("api/users/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}