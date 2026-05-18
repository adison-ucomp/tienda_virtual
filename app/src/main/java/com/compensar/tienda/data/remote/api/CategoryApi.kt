package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.CategoryDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApi {

    @GET("api/categories")
    suspend fun getAll(): List<CategoryDto>

    @GET("api/categories/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): CategoryDto

    @POST("api/categories")
    suspend fun insert(
        @Body category: CategoryDto
    ): CategoryDto

    @PUT("api/categories/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body category: CategoryDto
    ): CategoryDto

    @DELETE("api/categories/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}