package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.CategoryDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApi {

    @GET("api/category")
    suspend fun getAll(): List<CategoryDto>

    @GET("api/category/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): CategoryDto

    @POST("api/category")
    suspend fun insert(
        @Body category: CategoryDto
    ): CategoryDto

    @PUT("api/category/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body category: CategoryDto
    ): CategoryDto

    @DELETE("api/category/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}