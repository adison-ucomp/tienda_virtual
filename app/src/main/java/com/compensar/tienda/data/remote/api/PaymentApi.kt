package com.compensar.tienda.data.remote.api

import com.compensar.tienda.data.remote.dto.PaymentDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApi {

    @GET("api/payment")
    suspend fun getAll(): List<PaymentDto>

    @GET("api/payment/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): PaymentDto

    @POST("api/payment")
    suspend fun insert(
        @Body payment: PaymentDto
    ): PaymentDto

    @PUT("api/payment/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body payment: PaymentDto
    ): PaymentDto

    @DELETE("api/payment/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}