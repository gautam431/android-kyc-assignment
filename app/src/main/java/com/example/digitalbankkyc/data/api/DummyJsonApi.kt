package com.example.digitalbankkyc.data.api

import com.example.digitalbankkyc.data.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DummyJsonApi {

    @GET("users")
    suspend fun getUsers(
        @Query("limit") limit: Int = 20,
        @Query("skip")  skip: Int = 0
    ): UsersResponse
}