package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.ProductDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/product")
    suspend fun getAll(): List<ProductDto>

    @GET("api/product/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ProductDto

    @GET("api/product/category/{idCategory}")
    suspend fun getByCategory(
        @Path("idCategory") idCategory: Long
    ): List<ProductDto>

    @GET("api/product/shop/{idShop}")
    suspend fun getByShop(
        @Path("idShop") idShop: Long
    ): List<ProductDto>

    @GET("api/product/search")
    suspend fun searchByName(
        @Query("name") name: String
    ): List<ProductDto>

    @POST("api/product")
    suspend fun insert(
        @Body product: ProductDto
    ): ProductDto

    @PUT("api/product/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body product: ProductDto
    ): ProductDto

    @DELETE("api/product/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}