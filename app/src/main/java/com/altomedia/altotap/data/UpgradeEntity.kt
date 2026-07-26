package com.altomedia.altotap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_upgrades")
data class UpgradeEntity(
    @PrimaryKey val id: String, // "multiplier", "max_energy", "recharge_speed"
    val name: String,
    val currentLevel: Int,
    val maxLevel: Int,
    val costRp: Double,
    val description: String
)
