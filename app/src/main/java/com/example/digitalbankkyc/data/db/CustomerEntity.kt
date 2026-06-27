package com.example.digitalbankkyc.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val nationality: String,
    val address: String,
    val avatarUrl: String,
    val iban: String,
    val cardType: String,
    val currency: String,
    val balance: Double,
    val ifscCode: String,
    val kycStatus: String,          // "PENDING" or "VERIFIED"
    val selfieImagePath: String? = null,
    val cachedAt: Long = System.currentTimeMillis()
)