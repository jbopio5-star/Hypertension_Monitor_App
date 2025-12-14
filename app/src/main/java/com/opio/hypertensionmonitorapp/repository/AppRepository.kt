package com.opio.hypertensionmonitorapp.repository

import com.opio.hypertensionmonitorapp.data.AppDatabase
import com.opio.hypertensionmonitorapp.data.BpReading
import com.opio.hypertensionmonitorapp.data.User
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val db: AppDatabase
) {
    // User operations
    suspend fun insertUser(user: User): Long {
        return db.userDao().insert(user)
    }

    suspend fun getUserByPhone(phone: String): User? {
        return db.userDao().getUserByPhone(phone)
    }

    suspend fun getUserByPhoneAndPin(phone: String, pin: String): User? {
        return db.userDao().getUserByPhoneAndPin(phone, pin)
    }

    suspend fun getAllUsers(): List<User> {
        return db.userDao().getAllUsers()
    }

    // BP Reading operations
    suspend fun insertReading(reading: BpReading) {
        db.bpDao().insert(reading)
    }

    // FIX: Make this method suspend to match the DAO
    suspend fun getReadingsByUser(userId: Long): List<BpReading> {
        return db.bpDao().getReadingsByUser(userId)
    }

    suspend fun getLatestReading(userId: Long): BpReading? {
        return db.bpDao().getLatestForUser(userId)
    }

    // Session management
    private var currentUser: User? = null

    suspend fun getCurrentUser(): User? {
        return currentUser
    }

    suspend fun setCurrentUser(user: User) {
        currentUser = user
    }

    suspend fun clearCurrentUser() {
        currentUser = null
    }
}