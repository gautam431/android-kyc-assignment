package com.example.digitalbankkyc.data.repository

import com.example.digitalbankkyc.data.api.DummyJsonApi
import com.example.digitalbankkyc.data.api.IfscApi
import com.example.digitalbankkyc.data.db.CustomerDao
import com.example.digitalbankkyc.data.db.CustomerEntity
import com.example.digitalbankkyc.data.model.IfscResponse
import com.example.digitalbankkyc.domain.model.Customer
import com.example.digitalbankkyc.domain.model.KycStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

// Fixed list of IFSC codes to assign to customers
private val IFSC_CODES = listOf(
    "HDFC0CAGSBK",
    "SBIN0000001",
    "ICIC0000001",
    "PUNB0244200",
    "UTIB0000001"
)

// Cache expiry: 10 minutes
private const val CACHE_EXPIRY_MS = 10 * 60 * 1000L

@Singleton
class CustomerRepository @Inject constructor(
    private val dummyJsonApi: DummyJsonApi,
    private val ifscApi: IfscApi,
    private val customerDao: CustomerDao
) {

    // Fetch all customers — use cache if fresh, else fetch from API
    suspend fun getCustomers(forceRefresh: Boolean = false): List<Customer> {
        val cached = customerDao.getAllCustomers()

        // Always try to fetch from API first
        return try {
            val response = dummyJsonApi.getUsers(limit = 20)
            val entities = response.users.mapIndexed { index, user ->
                val existingKyc = cached.find { it.id == user.id }
                CustomerEntity(
                    id              = user.id,
                    firstName       = user.firstName,
                    lastName        = user.lastName,
                    email           = user.email,
                    phone           = user.phone,
                    dateOfBirth     = user.birthDate,
                    nationality     = user.nationality ?: "Indian",
                    address         = "${user.address.street}, ${user.address.city}, ${user.address.state}",
                    avatarUrl       = user.image,
                    iban            = user.bank.iban,
                    cardType        = user.bank.cardType,
                    currency        = user.bank.currency,
                    balance         = existingKyc?.balance ?: Random.nextDouble(1000.0, 200000.0),
                    ifscCode        = IFSC_CODES[index % IFSC_CODES.size],
                    kycStatus       = existingKyc?.kycStatus ?: "PENDING",
                    selfieImagePath = existingKyc?.selfieImagePath,
                    cachedAt        = System.currentTimeMillis()
                )
            }
            customerDao.clearAll()
            customerDao.insertAll(entities)
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            // If API fails, return whatever is in cache
            if (cached.isNotEmpty()) {
                cached.map { it.toDomain() }
            } else {
                throw e
            }
        }
    }

    suspend fun getCustomerById(id: Int): Customer? {
        return customerDao.getCustomerById(id)?.toDomain()
    }

    suspend fun getIfscDetails(ifsc: String): IfscResponse {
        return ifscApi.getIfscDetails(ifsc)
    }

    suspend fun updateKycVerified(id: Int, selfiePath: String) {
        customerDao.updateKycStatus(id, "VERIFIED", selfiePath)
    }
    suspend fun getCachedCustomers(): List<Customer> {
        return customerDao.getAllCustomers().map { it.toDomain() }
    }

    // Extension function to map DB entity → domain model
    private fun CustomerEntity.toDomain() = Customer(
        id              = id,
        firstName       = firstName,
        lastName        = lastName,
        email           = email,
        phone           = phone,
        dateOfBirth     = dateOfBirth,
        nationality     = nationality,
        address         = address,
        avatarUrl       = avatarUrl,
        iban            = iban,
        cardType        = cardType,
        currency        = currency,
        balance         = balance,
        ifscCode        = ifscCode,
        kycStatus       = if (kycStatus == "VERIFIED") KycStatus.VERIFIED else KycStatus.PENDING,
        selfieImagePath = selfieImagePath
    )
}