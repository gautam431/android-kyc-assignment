package com.example.digitalbankkyc.data.db

import androidx.room.*

@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers")
    suspend fun getAllCustomers(): List<CustomerEntity>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerEntity>)

    @Query("UPDATE customers SET kycStatus = :status, selfieImagePath = :selfiePath WHERE id = :id")
    suspend fun updateKycStatus(id: Int, status: String, selfiePath: String?)

    @Query("DELETE FROM customers")
    suspend fun clearAll()
}