package com.opio.hypertensionmonitorapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE phone = :phone AND pin = :pin LIMIT 1")
    suspend fun getUserByPhoneAndPin(phone: String, pin: String): User?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // Keep the login method for backward compatibility
    @Query("SELECT * FROM users WHERE phone = :phone AND pin = :pin LIMIT 1")
    suspend fun login(phone: String, pin: String): User?
}