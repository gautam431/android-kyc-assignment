package com.example.digitalbankkyc.data.api

import com.example.digitalbankkyc.data.model.IfscResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface IfscApi {

    @GET("{ifsc}")
    suspend fun getIfscDetails(
        @Path("ifsc") ifsc: String
    ): IfscResponse
}