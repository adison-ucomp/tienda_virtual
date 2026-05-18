package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    @GET("api/user")
    suspend fun getAll(): List<UserDto>

    @GET("api/user/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): UserDto

    @GET("api/user/email/{email}")
    suspend fun getByEmail(
        @Path("email") email: String
    ): UserDto

    @GET("api/user/role/{idRole}")
    suspend fun getByRole(
        @Path("idRole") idRole: Long
    ): List<UserDto>

    @POST("api/user")
    suspend fun insert(
        @Body user: UserDto
    ): UserDto

    @PUT("api/user/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body user: UserDto
    ): UserDto

    @DELETE("api/user/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}