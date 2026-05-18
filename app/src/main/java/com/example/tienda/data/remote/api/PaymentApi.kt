package com.example.tienda.data.remote.api

import com.example.tienda.data.remote.dto.PaymentDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PaymentApi {

    @GET("api/payments")
    suspend fun getAll(): List<PaymentDto>

    @GET("api/payments/{register}")
    suspend fun getByRegister(
        @Path("register") register: Long
    ): PaymentDto

    @POST("api/payments")
    suspend fun insert(
        @Body payment: PaymentDto
    ): PaymentDto

    @PUT("api/payments/{register}")
    suspend fun update(
        @Path("register") register: Long,
        @Body payment: PaymentDto
    ): PaymentDto

    @DELETE("api/payments/{register}")
    suspend fun delete(
        @Path("register") register: Long
    )
}