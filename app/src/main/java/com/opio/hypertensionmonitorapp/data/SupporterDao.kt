package com.opio.hypertensionmonitorapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SupporterDao {
    @Insert
    suspend fun insert(supporter: Supporter)

    @Query("SELECT * FROM supporters WHERE userId = :userId LIMIT 1")
    suspend fun getSupporterForUser(userId: Long): Supporter?
}