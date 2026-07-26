package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "robots")
data class RobotEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val earningsPerSec: Double,
    val rentPriceRp: Double,
    val isRented: Boolean = false,
    val rentedUntilTimestamp: Long = 0L
)
