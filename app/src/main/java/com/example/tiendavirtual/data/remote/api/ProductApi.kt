package com.example.tiendavirtual.data.remote.api

import com.example.tiendavirtual.data.remote.dto.ProductDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("api/products")
    suspend fun getAll(): List<ProductDto>

    @GET("api/products/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): ProductDto

    @GET("api/products/category/{idCategory}")
    suspend fun getByCategory(
        @Path("idCategory") idCategory: Long
    ): List<ProductDto>

    @GET("api/products/shop/{idShop}")
    suspend fun getByShop(
        @Path("idShop") idShop: Long
    ): List<ProductDto>

    @GET("api/products/search")
    suspend fun searchByName(
        @Query("name") name: String
    ): List<ProductDto>

    @POST("api/products")
    suspend fun insert(
        @Body product: ProductDto
    ): ProductDto

    @PUT("api/products/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body product: ProductDto
    ): ProductDto

    @DELETE("api/products/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}