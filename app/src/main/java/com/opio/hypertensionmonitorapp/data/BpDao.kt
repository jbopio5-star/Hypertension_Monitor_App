package com.opio.hypertensionmonitorapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BpDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: BpReading)

    @Query("SELECT * FROM bp_readings WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllForUser(userId: Long): Flow<List<BpReading>>

    @Query("SELECT * FROM bp_readings WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestForUser(userId: Long): BpReading?

    // Add this method for non-Flow access
    @Query("SELECT * FROM bp_readings WHERE userId = :userId ORDER BY timestamp DESC")
    suspend fun getReadingsByUser(userId: Long): List<BpReading>
}