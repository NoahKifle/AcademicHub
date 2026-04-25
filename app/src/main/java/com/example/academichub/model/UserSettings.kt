package com.example.academichub.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 0,
    val userName: String = "",
    val semesterStartDate: String = "",
    val semesterEndDate: String = "",
    val targetGpa: Double = 4.0,
    val currentGpa: Double = 0.0
)
