package com.opio.hypertensionmonitorapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bp_readings")
data class BpReading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val systolic: Int,
    val diastolic: Int,
    val heartRate: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isManual: Boolean = false,  // This is fine
    val notes: String = ""
)