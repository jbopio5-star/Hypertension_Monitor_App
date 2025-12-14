package com.opio.hypertensionmonitorapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supporters")
data class Supporter(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val userId: Long,        // belongs to which patient
    val name: String,
    val sex: String,         // "Male" or "Female"
    val phone1: String,      // main contact
    val phone2: String = ""  // optional second number
)