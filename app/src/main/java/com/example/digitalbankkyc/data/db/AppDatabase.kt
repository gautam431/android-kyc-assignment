package com.example.digitalbankkyc.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CustomerEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
}