package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.AddressDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApi {

    @GET("api/addresses")
    suspend fun getAll(): List<AddressDto>

    @GET("api/addresses/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): AddressDto

    @GET("api/addresses/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): List<AddressDto>

    @POST("api/addresses")
    suspend fun insert(
        @Body address: AddressDto
    ): AddressDto

    @PUT("api/addresses/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body address: AddressDto
    ): AddressDto

    @DELETE("api/addresses/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}