package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // User Stats
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(userStats: UserStatsEntity)

    // Upgrades
    @Query("SELECT * FROM system_upgrades")
    fun getAllUpgrades(): Flow<List<UpgradeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpgrades(upgrades: List<UpgradeEntity>)

    @Update
    suspend fun updateUpgrade(upgrade: UpgradeEntity)

    // Robots
    @Query("SELECT * FROM robots")
    fun getAllRobots(): Flow<List<RobotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobots(robots: List<RobotEntity>)

    @Update
    suspend fun updateRobot(robot: RobotEntity)

    // Tasks
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    // History
    @Query("SELECT * FROM history_logs ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    // Withdrawals
    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity)
}
