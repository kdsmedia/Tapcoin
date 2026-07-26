package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val rewardRp: Double,
    val isDaily: Boolean, // true = TUGAS HARIAN, false = TUGAS WAJIB
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)
