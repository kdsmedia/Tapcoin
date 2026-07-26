package com.altomedia.altotap.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 1,
    val balanceRp: Double = 1000.0,
    val energy: Int = 500,
    val maxEnergy: Int = 500,
    val pointsPerTap: Int = 1,
    val totalTaps: Int = 0,
    val completedTasks: Int = 0,
    val invitedFriends: Int = 0,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhotoUrl: String = "",
    val tapMultiplierLevel: Int = 1,
    val maxEnergyLevel: Int = 1,
    val energyRechargeLevel: Int = 1,
    val spinAttemptsLeft: Int = 3,
    val spinAdsRemaining: Int = 3
)
