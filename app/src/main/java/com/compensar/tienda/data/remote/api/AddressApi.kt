package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.AddressDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AddressApi {

    @GET("api/address")
    suspend fun getAll(): List<AddressDto>

    @GET("api/address/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): AddressDto

    @GET("api/address/user/{idUser}")
    suspend fun getByUser(
        @Path("idUser") idUser: Long
    ): List<AddressDto>

    @POST("api/address")
    suspend fun insert(
        @Body address: AddressDto
    ): AddressDto

    @PUT("api/address/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body address: AddressDto
    ): AddressDto

    @DELETE("api/address/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}