package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_logs")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amountRp: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String // "UPGRADE", "ROBOT", "TASK", "SPIN", "WITHDRAWAL"
)
