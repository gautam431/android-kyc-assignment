package com.example.digitalbankkyc.data.model

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    @SerializedName("users") val users: List<UserDto>
)

data class UserDto(
    @SerializedName("id")          val id: Int,
    @SerializedName("firstName")   val firstName: String,
    @SerializedName("lastName")    val lastName: String,
    @SerializedName("email")       val email: String,
    @SerializedName("phone")       val phone: String,
    @SerializedName("birthDate")   val birthDate: String,
    @SerializedName("nationality") val nationality: String? = "Indian",
    @SerializedName("image")       val image: String,
    @SerializedName("address")     val address: AddressDto,
    @SerializedName("bank")        val bank: BankDto
)

data class AddressDto(
    @SerializedName("address") val street: String,
    @SerializedName("city")    val city: String,
    @SerializedName("state")   val state: String,
    @SerializedName("country") val country: String
)

data class BankDto(
    @SerializedName("iban")       val iban: String,
    @SerializedName("cardType")   val cardType: String,
    @SerializedName("currency")   val currency: String
)