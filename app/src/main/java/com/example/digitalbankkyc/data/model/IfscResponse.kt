package com.example.digitalbankkyc.data.model

import com.google.gson.annotations.SerializedName

data class IfscResponse(
    @SerializedName("BANK")   val bank: String,
    @SerializedName("BRANCH") val branch: String,
    @SerializedName("CITY")   val city: String,
    @SerializedName("STATE")  val state: String,
    @SerializedName("IFSC")   val ifsc: String
)