package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val paymentMethod: String, // "DANA", "OVO", "GOPAY"
    val nominalRp: Double,
    val accountNumber: String,
    val accountName: String,
    val status: String = "Selesai", // "Selesai" or "Proses"
    val timestamp: Long = System.currentTimeMillis()
)
