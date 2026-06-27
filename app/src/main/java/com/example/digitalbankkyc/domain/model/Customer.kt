package com.example.digitalbankkyc.domain.model

data class Customer(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val nationality: String,
    val address: String,
    val avatarUrl: String,
    val iban: String,           // masked account number
    val cardType: String,       // Savings / Current / NRI etc.
    val currency: String,
    val balance: Double,
    val ifscCode: String,
    val kycStatus: KycStatus,
    val selfieImagePath: String? = null   // path saved after selfie
)

enum class KycStatus {
    PENDING, VERIFIED
}