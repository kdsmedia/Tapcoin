package com.altomedia.altotap.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(private val appDao: AppDao) {

    val userStats: Flow<UserStatsEntity?> = appDao.getUserStats()
    val upgrades: Flow<List<UpgradeEntity>> = appDao.getAllUpgrades()
    val robots: Flow<List<RobotEntity>> = appDao.getAllRobots()
    val tasks: Flow<List<TaskEntity>> = appDao.getAllTasks()
    val history: Flow<List<HistoryEntity>> = appDao.getAllHistory()
    val withdrawals: Flow<List<WithdrawalEntity>> = appDao.getAllWithdrawals()

    suspend fun seedInitialDataIfEmpty() {
        // Seed UserStats if not present or sanitize 6-digit referral code
        val currentStats = appDao.getUserStats().firstOrNull()
        if (currentStats == null) {
            val randomCode = (100000..999999).random().toString()
            appDao.insertOrUpdateUserStats(UserStatsEntity(userId = randomCode))
        } else if (currentStats.userId.startsWith("CUAN") || currentStats.userId.length != 6 || !currentStats.userId.all { it.isDigit() }) {
            val randomCode = (100000..999999).random().toString()
            appDao.insertOrUpdateUserStats(currentStats.copy(userId = randomCode))
        }

        // Seed Upgrades if empty or update to new structure
        val currentUpgrades = appDao.getAllUpgrades().firstOrNull()
        if (currentUpgrades.isNullOrEmpty()) {
            appDao.insertUpgrades(
                listOf(
                    UpgradeEntity(
                        id = "multiplier",
                        name = "Daya Ketuk (+1 Koin)",
                        currentLevel = 1,
                        maxLevel = 50,
                        costRp = 500.0,
                        description = "Tambah +1 koin per ketukan (Level 1-50)"
                    ),
                    UpgradeEntity(
                        id = "max_energy",
                        name = "Kapasitas Energi",
                        currentLevel = 1,
                        maxLevel = 50,
                        costRp = 1000.0,
                        description = "Tambah +50 batas energi maks"
                    ),
                    UpgradeEntity(
                        id = "recharge_speed",
                        name = "Isi Ulang Baterai",
                        currentLevel = 1,
                        maxLevel = 50,
                        costRp = 1500.0,
                        description = "Percepat pemulihan energi baterai"
                    )
                )
            )
        } else {
            currentUpgrades.forEach { upgrade ->
                val newBaseCost = when (upgrade.id) {
                    "multiplier" -> 500.0
                    "max_energy" -> 1000.0
                    "recharge_speed" -> 1500.0
                    else -> 1000.0
                }
                val calculatedCost = newBaseCost * Math.pow(1.25, (upgrade.currentLevel - 1).coerceAtLeast(0).toDouble())
                appDao.updateUpgrade(
                    upgrade.copy(
                        name = when (upgrade.id) {
                            "multiplier" -> "Daya Ketuk (+1 Koin)"
                            "max_energy" -> "Kapasitas Energi"
                            "recharge_speed" -> "Isi Ulang Baterai"
                            else -> upgrade.name
                        },
                        maxLevel = 50,
                        costRp = calculatedCost
                    )
                )
            }
        }

        // Seed Robots (Robot auto-taps at default 1 tap = 1 koin, fixed per sec)
        val currentRobots = appDao.getAllRobots().firstOrNull()
        if (currentRobots.isNullOrEmpty()) {
            appDao.insertRobots(
                listOf(
                    RobotEntity(
                        id = 1,
                        name = "Bot v1",
                        earningsPerSec = 1.0, // 1 ketuk/detik = 1 koin/detik (1 ketuk = 1 koin)
                        rentPriceRp = 2000.0,
                        isRented = false
                    ),
                    RobotEntity(
                        id = 2,
                        name = "Bot Pro",
                        earningsPerSec = 5.0, // 5 ketuk/detik = 5 koin/detik
                        rentPriceRp = 8000.0,
                        isRented = false
                    ),
                    RobotEntity(
                        id = 3,
                        name = "Bot Cyber",
                        earningsPerSec = 20.0, // 20 ketuk/detik = 20 koin/detik
                        rentPriceRp = 30000.0,
                        isRented = false
                    )
                )
            )
        } else {
            currentRobots.forEach { robot ->
                val newPrice = when (robot.id) {
                    1 -> 2000.0
                    2 -> 8000.0
                    3 -> 30000.0
                    else -> robot.rentPriceRp
                }
                val newEarning = when (robot.id) {
                    1 -> 1.0
                    2 -> 5.0
                    3 -> 20.0
                    else -> robot.earningsPerSec
                }
                appDao.updateRobot(robot.copy(rentPriceRp = newPrice, earningsPerSec = newEarning))
            }
        }

        // Seed Tasks if empty
        val currentTasks = appDao.getAllTasks().firstOrNull()
        if (currentTasks.isNullOrEmpty()) {
            appDao.insertTasks(
                listOf(
                    // TUGAS HARIAN
                    TaskEntity(id = 1, title = "Ketuk 50 Kali", rewardRp = 500.0, isDaily = true, progress = 0, maxProgress = 50),
                    TaskEntity(id = 2, title = "Check-in Harian", rewardRp = 1000.0, isDaily = true, progress = 0, maxProgress = 1),
                    TaskEntity(id = 3, title = "Putar Roda 1 Kali", rewardRp = 800.0, isDaily = true, progress = 0, maxProgress = 1),
                    TaskEntity(id = 4, title = "Tonton 3 Video Iklan Cuan", rewardRp = 5000.0, isDaily = true, progress = 0, maxProgress = 3),
                    TaskEntity(id = 5, title = "Sewa 1 Robot", rewardRp = 3000.0, isDaily = true, progress = 0, maxProgress = 1),
                    TaskEntity(id = 6, title = "Undang 1 Teman", rewardRp = 5000.0, isDaily = true, progress = 0, maxProgress = 1),

                    // TUGAS WAJIB
                    TaskEntity(id = 101, title = "Gabung Telegram Cuan", rewardRp = 2500.0, isDaily = false, progress = 0, maxProgress = 1),
                    TaskEntity(id = 102, title = "Follow Instagram Official", rewardRp = 2500.0, isDaily = false, progress = 0, maxProgress = 1),
                    TaskEntity(id = 103, title = "Subscribe Channel YouTube", rewardRp = 3500.0, isDaily = false, progress = 0, maxProgress = 1),

                    // TUGAS WAJIB — Sosial Media
                    TaskEntity(id = 104, title = "Subscribe YouTube Sidhanie", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1),
                    TaskEntity(id = 105, title = "Follow TikTok AltoMedia", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1),
                    TaskEntity(id = 106, title = "Follow Instagram Sidhanie", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1)
                )
            )
        } else {
            // Pastikan tugas sosial media ada untuk pengguna lama
            val existingIds = currentTasks.map { it.id }.toSet()
            val missingTasks = mutableListOf<TaskEntity>()
            if (104 !in existingIds) missingTasks.add(TaskEntity(id = 104, title = "Subscribe YouTube Sidhanie", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1))
            if (105 !in existingIds) missingTasks.add(TaskEntity(id = 105, title = "Follow TikTok AltoMedia", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1))
            if (106 !in existingIds) missingTasks.add(TaskEntity(id = 106, title = "Follow Instagram Sidhanie", rewardRp = 5000.0, isDaily = false, progress = 0, maxProgress = 1))
            if (missingTasks.isNotEmpty()) appDao.insertTasks(missingTasks)
        }
    }

    suspend fun saveUserStats(stats: UserStatsEntity) {
        appDao.insertOrUpdateUserStats(stats)
    }

    suspend fun addHistory(title: String, amountRp: Double, category: String) {
        appDao.insertHistory(
            HistoryEntity(
                title = title,
                amountRp = amountRp,
                category = category
            )
        )
    }

    suspend fun updateUpgrade(upgrade: UpgradeEntity) {
        appDao.updateUpgrade(upgrade)
    }

    suspend fun updateRobot(robot: RobotEntity) {
        appDao.updateRobot(robot)
    }

    suspend fun updateTask(task: TaskEntity) {
        appDao.updateTask(task)
    }

    suspend fun addWithdrawal(withdrawal: WithdrawalEntity) {
        appDao.insertWithdrawal(withdrawal)
    }
}
