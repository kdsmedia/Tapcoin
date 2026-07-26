package com.altomedia.altotap.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.altomedia.altotap.data.AppDatabase
import com.altomedia.altotap.data.GameRepository
import com.altomedia.altotap.data.HistoryEntity
import com.altomedia.altotap.data.RobotEntity
import com.altomedia.altotap.data.TaskEntity
import com.altomedia.altotap.data.UpgradeEntity
import com.altomedia.altotap.data.UserStatsEntity
import com.altomedia.altotap.data.WithdrawalEntity
import com.altomedia.altotap.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import kotlin.random.Random

data class TapFloatingText(
    val id: Long = System.currentTimeMillis() + Random.nextLong(1000),
    val text: String,
    val offsetX: Float,
    val offsetY: Float
)

data class SpinResult(
    val title: String,
    val amountRp: Double,
    val isZonk: Boolean,
    val winningIndex: Int
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val soundManager = SoundManager.getInstance(application)

    val userStats: StateFlow<UserStatsEntity?>
    val upgrades: StateFlow<List<UpgradeEntity>>
    val robots: StateFlow<List<RobotEntity>>
    val tasks: StateFlow<List<TaskEntity>>
    val history: StateFlow<List<HistoryEntity>>
    val withdrawals: StateFlow<List<WithdrawalEntity>>

    // Current page index:
    // 0 = Home (Beranda), 1 = Tasks (Tugas), 2 = Upgrade, 3 = Referral (Tautan Refrensi), 4 = Profile (Akun)
    // 5 = Spin Wheel (Gift Icon), 6 = Penarikan (Wallet Icon)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _floatingTexts = MutableStateFlow<List<TapFloatingText>>(emptyList())
    val floatingTexts: StateFlow<List<TapFloatingText>> = _floatingTexts.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _spinTargetAngle = MutableStateFlow(0f)
    val spinTargetAngle: StateFlow<Float> = _spinTargetAngle.asStateFlow()

    private val _pendingSpinResult = MutableStateFlow<SpinResult?>(null)
    val pendingSpinResult: StateFlow<SpinResult?> = _pendingSpinResult.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).appDao()
        repository = GameRepository(dao)

        val tempStats = MutableStateFlow<UserStatsEntity?>(null)
        val tempUpgrades = MutableStateFlow<List<UpgradeEntity>>(emptyList())
        val tempRobots = MutableStateFlow<List<RobotEntity>>(emptyList())
        val tempTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
        val tempHistory = MutableStateFlow<List<HistoryEntity>>(emptyList())
        val tempWithdrawals = MutableStateFlow<List<WithdrawalEntity>>(emptyList())

        userStats = tempStats.asStateFlow()
        upgrades = tempUpgrades.asStateFlow()
        robots = tempRobots.asStateFlow()
        tasks = tempTasks.asStateFlow()
        history = tempHistory.asStateFlow()
        withdrawals = tempWithdrawals.asStateFlow()

        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()

            launch { repository.userStats.collectLatest { tempStats.value = it } }
            launch { repository.upgrades.collectLatest { tempUpgrades.value = it } }
            launch { repository.robots.collectLatest { tempRobots.value = it } }
            launch { repository.tasks.collectLatest { tempTasks.value = it } }
            launch { repository.history.collectLatest { tempHistory.value = it } }
            launch { repository.withdrawals.collectLatest { tempWithdrawals.value = it } }

            // Start energy recovery and robot passive income tickers
            launch { startTickers() }
        }
    }

    fun navigateTo(tabIndex: Int) {
        _currentTab.value = tabIndex
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun onAdClicked(adTitle: String) {
        val currentStats = userStats.value ?: return
        val newEnergy = (currentStats.energy + 100).coerceAtMost(currentStats.maxEnergy)
        val newBalance = currentStats.balanceRp + 1500.0
        val newSpins = currentStats.spinAttemptsLeft + 1

        val updatedStats = currentStats.copy(
            energy = newEnergy,
            balanceRp = newBalance,
            spinAttemptsLeft = newSpins
        )
        viewModelScope.launch {
            repository.saveUserStats(updatedStats)
            repository.addHistory(
                title = "Bonus Iklan Cuan",
                amountRp = 1500.0,
                category = "AD"
            )

            // Progress task "Tonton 3 Video Iklan Cuan"
            val adTask = tasks.value.find { it.id == 4 }
            if (adTask != null && !adTask.isCompleted) {
                val newProg = (adTask.progress + 1).coerceAtMost(adTask.maxProgress)
                repository.updateTask(adTask.copy(progress = newProg))
            }
        }
        _userMessage.value = "Bonus Iklan Cuan: +1.500 KOIN, +100 Energi & +1 Spin Gratis!"
    }

    private suspend fun startTickers() {
        while (true) {
            delay(1000L)
            val current = userStats.value ?: continue

            var updatedBalance = current.balanceRp
            var updatedEnergy = current.energy

            // 1. Passive Income from rented robots (default 1 tap = 1 koin, fixed rate)
            val rentedRobots = robots.value.filter { it.isRented }
            if (rentedRobots.isNotEmpty()) {
                val passiveEarned = rentedRobots.sumOf { it.earningsPerSec }
                if (passiveEarned > 0) {
                    updatedBalance += passiveEarned
                }
            }

            // 2. Energy recovery based on recharge level
            val recoveryRate = current.energyRechargeLevel * 2
            if (updatedEnergy < current.maxEnergy) {
                updatedEnergy = (updatedEnergy + recoveryRate).coerceAtMost(current.maxEnergy)
            }

            if (updatedBalance != current.balanceRp || updatedEnergy != current.energy) {
                repository.saveUserStats(
                    current.copy(
                        balanceRp = updatedBalance,
                        energy = updatedEnergy
                    )
                )
            }
        }
    }

    fun onTapCoin(x: Float = 0f, y: Float = 0f) {
        val current = userStats.value ?: return
        if (current.energy <= 0) {
            _userMessage.value = "Energi habis! Tunggu beberapa saat untuk isi ulang."
            return
        }

        soundManager.playCoinTap()

        // 1 ketuk = 1 koin (Level 1), bertambah +1 koin tiap level upgrade up to Level 50
        val points = current.tapMultiplierLevel.coerceAtLeast(1)
        val earned = points.toDouble()
        val newBalance = current.balanceRp + earned
        val newEnergy = (current.energy - 1).coerceAtLeast(0)
        val newTotalTaps = current.totalTaps + 1

        viewModelScope.launch {
            repository.saveUserStats(
                current.copy(
                    balanceRp = newBalance,
                    energy = newEnergy,
                    pointsPerTap = points,
                    totalTaps = newTotalTaps
                )
            )

            // Update "Ketuk 50 Kali" task progress if active
            val tapTask = tasks.value.find { it.id == 1 }
            if (tapTask != null && !tapTask.isCompleted) {
                val newProg = tapTask.progress + 1
                repository.updateTask(tapTask.copy(progress = newProg))
            }

            // Add floating tap animation
            val textItem = TapFloatingText(
                text = "+$points KOIN",
                offsetX = (Random.nextFloat() - 0.5f) * 120f,
                offsetY = (Random.nextFloat() - 0.5f) * 80f
            )
            _floatingTexts.value = _floatingTexts.value + textItem

            // Remove float text after delay
            delay(800)
            _floatingTexts.value = _floatingTexts.value.filter { it.id != textItem.id }
        }
    }

    fun calculateUpgradeCost(upgradeId: String, level: Int): Double {
        val baseCost = when (upgradeId) {
            "multiplier" -> 500.0
            "max_energy" -> 1000.0
            "recharge_speed" -> 1500.0
            else -> 1000.0
        }
        val multiplier = Math.pow(1.25, (level - 1).coerceAtLeast(0).toDouble())
        return baseCost * multiplier
    }

    fun buyUpgrade(upgradeId: String) {
        val currentStats = userStats.value ?: return
        val upgradeItem = upgrades.value.find { it.id == upgradeId } ?: return

        if (upgradeItem.currentLevel >= 50) {
            _userMessage.value = "Level upgrade sudah mencapai batas maksimum (Lv. 50)!"
            return
        }

        val currentCost = calculateUpgradeCost(upgradeId, upgradeItem.currentLevel)

        if (currentStats.balanceRp < currentCost) {
            _userMessage.value = "Saldo KOIN tidak mencukupi untuk upgrade ini!"
            return
        }

        viewModelScope.launch {
            val newBalance = currentStats.balanceRp - currentCost
            val newLevel = upgradeItem.currentLevel + 1
            val nextCost = calculateUpgradeCost(upgradeId, newLevel)

            var updatedStats = currentStats.copy(balanceRp = newBalance)

            when (upgradeId) {
                "multiplier" -> {
                    // Level 1 = 1 koin, Level 2 = 2 koin, ... Level 50 = 50 koin
                    val newPoints = newLevel
                    updatedStats = updatedStats.copy(
                        pointsPerTap = newPoints,
                        tapMultiplierLevel = newLevel
                    )
                }
                "max_energy" -> {
                    val newMaxEnergy = currentStats.maxEnergy + 50
                    updatedStats = updatedStats.copy(
                        maxEnergy = newMaxEnergy,
                        energy = (currentStats.energy + 50).coerceAtMost(newMaxEnergy),
                        maxEnergyLevel = newLevel
                    )
                }
                "recharge_speed" -> {
                    updatedStats = updatedStats.copy(energyRechargeLevel = newLevel)
                }
            }

            repository.saveUserStats(updatedStats)
            repository.updateUpgrade(
                upgradeItem.copy(
                    currentLevel = newLevel,
                    costRp = nextCost
                )
            )

            repository.addHistory(
                title = "Upgrade ${upgradeItem.name} Lv.$newLevel",
                amountRp = -currentCost,
                category = "UPGRADE"
            )

            soundManager.playUpgradeSuccess()
            _userMessage.value = "Berhasil upgrade ${upgradeItem.name} ke Level $newLevel!"
        }
    }

    fun rentRobot(robotId: Int) {
        val currentStats = userStats.value ?: return
        val robot = robots.value.find { it.id == robotId } ?: return

        if (robot.isRented) {
            _userMessage.value = "${robot.name} sudah aktif bekerja!"
            return
        }

        if (currentStats.balanceRp < robot.rentPriceRp) {
            _userMessage.value = "Saldo KOIN tidak mencukupi untuk menyewa ${robot.name}!"
            return
        }

        viewModelScope.launch {
            val newBalance = currentStats.balanceRp - robot.rentPriceRp
            repository.saveUserStats(currentStats.copy(balanceRp = newBalance))

            repository.updateRobot(
                robot.copy(
                    isRented = true,
                    rentedUntilTimestamp = System.currentTimeMillis() + 86400000L
                )
            )

            repository.addHistory(
                title = "Sewa ${robot.name}",
                amountRp = -robot.rentPriceRp,
                category = "ROBOT"
            )

            // Progress task "Sewa 1 Robot"
            val robotTask = tasks.value.find { it.id == 5 }
            if (robotTask != null && !robotTask.isCompleted) {
                repository.updateTask(robotTask.copy(progress = 1))
            }

            soundManager.playUpgradeSuccess()
            _userMessage.value = "Berhasil menyewa ${robot.name}! Otomatis mengumpulkan ${robot.earningsPerSec.toInt()} KOIN/detik."
        }
    }

    fun claimTaskReward(taskId: Int) {
        val currentStats = userStats.value ?: return
        val task = tasks.value.find { it.id == taskId } ?: return

        if (task.isCompleted) {
            _userMessage.value = "Tugas ini sudah diklaim!"
            return
        }

        viewModelScope.launch {
            val newBalance = currentStats.balanceRp + task.rewardRp
            val newCompletedTasks = currentStats.completedTasks + 1

            repository.saveUserStats(
                currentStats.copy(
                    balanceRp = newBalance,
                    completedTasks = newCompletedTasks
                )
            )

            repository.updateTask(
                task.copy(
                    isCompleted = true,
                    progress = task.maxProgress
                )
            )

            repository.addHistory(
                title = "Klaim Tugas: ${task.title}",
                amountRp = task.rewardRp,
                category = "TASK"
            )

            soundManager.playUpgradeSuccess()
            _userMessage.value = "Berhasil klaim hadiah ${task.rewardRp.toInt()} KOIN!"
        }
    }

    fun watchAdForTask(taskId: Int) {
        val currentStats = userStats.value ?: return
        val task = tasks.value.find { it.id == taskId } ?: return
        if (task.isCompleted) return

        viewModelScope.launch {
            val newProgress = (task.progress + 1).coerceAtMost(task.maxProgress)
            val isNowCompleted = newProgress >= task.maxProgress

            val updatedTask = task.copy(
                progress = newProgress,
                isCompleted = isNowCompleted
            )
            repository.updateTask(updatedTask)

            if (isNowCompleted) {
                val newBalance = currentStats.balanceRp + task.rewardRp
                val newCompletedTasks = currentStats.completedTasks + 1
                repository.saveUserStats(
                    currentStats.copy(
                        balanceRp = newBalance,
                        completedTasks = newCompletedTasks
                    )
                )
                repository.addHistory(
                    title = "Klaim Tugas: ${task.title}",
                    amountRp = task.rewardRp,
                    category = "TASK"
                )
                _userMessage.value = "Selamat! Tugas '${task.title}' selesai (+${task.rewardRp.toInt()} KOIN)"
            } else {
                _userMessage.value = "Kemajuan Iklan Tugas: $newProgress/${task.maxProgress}"
            }
        }
    }

    fun clearPendingSpinResult() {
        _pendingSpinResult.value = null
    }

    fun claimSpinReward() {
        val result = _pendingSpinResult.value ?: return
        _pendingSpinResult.value = null

        if (result.isZonk) return

        viewModelScope.launch {
            val statsNow = userStats.value ?: return@launch
            val newBalance = statsNow.balanceRp + result.amountRp

            repository.saveUserStats(statsNow.copy(balanceRp = newBalance))
            repository.addHistory(
                title = "Hadiah Putar Roda (${result.title})",
                amountRp = result.amountRp,
                category = "SPIN"
            )

            // Progress task "Putar Roda 1 Kali"
            val spinTask = tasks.value.find { it.id == 3 }
            if (spinTask != null && !spinTask.isCompleted) {
                repository.updateTask(spinTask.copy(progress = 1))
            }

            _userMessage.value = "Berhasil klaim hadiah spin: +${result.amountRp.toInt()} KOIN!"
        }
    }

    fun watchAdForSpin() {
        val currentStats = userStats.value ?: return
        if (currentStats.spinAdsRemaining <= 0) {
            _userMessage.value = "Kesempatan iklan putaran gratis sudah habis (0/3)!"
            return
        }

        val newSpinAttempts = currentStats.spinAttemptsLeft + 1
        val newAdsRemaining = (currentStats.spinAdsRemaining - 1).coerceAtLeast(0)

        val updatedStats = currentStats.copy(
            spinAttemptsLeft = newSpinAttempts,
            spinAdsRemaining = newAdsRemaining
        )

        viewModelScope.launch {
            repository.saveUserStats(updatedStats)
            repository.addHistory(
                title = "Bonus Spin Iklan",
                amountRp = 0.0,
                category = "AD"
            )
            _userMessage.value = "Selamat! Anda mendapatkan +1 Spin Gratis! (Sisa Kesempatan Iklan: $newAdsRemaining/3)"
        }
    }

    fun spinWheel() {
        if (_isSpinning.value) return
        val currentStats = userStats.value ?: return

        if (currentStats.spinAttemptsLeft <= 0 && currentStats.energy < 10) {
            _userMessage.value = "Kesempatan spin habis & energi kurang dari 10!"
            return
        }

        _isSpinning.value = true

        viewModelScope.launch {
            // Deduct spin attempt or energy
            var newSpinAttempts = currentStats.spinAttemptsLeft
            var newEnergy = currentStats.energy

            if (newSpinAttempts > 0) {
                newSpinAttempts -= 1
            } else {
                newEnergy = (newEnergy - 10).coerceAtLeast(0)
            }

            repository.saveUserStats(
                currentStats.copy(
                    spinAttemptsLeft = newSpinAttempts,
                    energy = newEnergy
                )
            )

            // Spin prizes on 8 slices:
            // 0: Rp.250, 1: Rp.100, 2: 200 KOIN, 3: 50 KOIN, 4: Rp.50, 5: 250 KOIN, 6: 500 KOIN, 7: ZONK
            val winningIndex = Random.nextInt(8)
            val segmentAngle = 360f / 8f
            val randomRounds = Random.nextInt(5, 8) * 360f
            val targetAngle = randomRounds + (winningIndex * segmentAngle) + (segmentAngle / 2f)

            _spinTargetAngle.value = _spinTargetAngle.value + targetAngle

            delay(3500L) // Wait for spin animation

            _isSpinning.value = false

            val prizeTitle = when (winningIndex) {
                0 -> "250 KOIN"
                1 -> "100 KOIN"
                2 -> "200 KOIN"
                3 -> "50 KOIN"
                4 -> "150 KOIN"
                5 -> "250 KOIN"
                6 -> "500 KOIN"
                else -> "ZONK"
            }

            val amount = when (winningIndex) {
                0 -> 250.0
                1 -> 100.0
                2 -> 200.0
                3 -> 50.0
                4 -> 150.0
                5 -> 250.0
                6 -> 500.0
                else -> 0.0
            }

            _pendingSpinResult.value = SpinResult(
                title = prizeTitle,
                amountRp = amount,
                isZonk = (winningIndex == 7),
                winningIndex = winningIndex
            )
        }
    }

    fun submitWithdrawal(paymentMethod: String, nominalRp: Double, accountNumber: String, accountName: String) {
        val currentStats = userStats.value ?: return

        if (accountNumber.isBlank() || accountName.isBlank()) {
            _userMessage.value = "Mohon isi Nomor HP & Nama Pemilik E-Wallet!"
            return
        }

        val koinRequired = nominalRp * 1000.0
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val rpStr = formatter.format(nominalRp.toLong())

        if (currentStats.balanceRp < koinRequired) {
            val koinStr = formatter.format(koinRequired.toLong())
            _userMessage.value = "Saldo KOIN tidak mencukupi! Butuh $koinStr KOIN untuk penarikan Rp $rpStr."
            return
        }

        viewModelScope.launch {
            val newBalance = currentStats.balanceRp - koinRequired
            repository.saveUserStats(currentStats.copy(balanceRp = newBalance))

            repository.addWithdrawal(
                WithdrawalEntity(
                    paymentMethod = paymentMethod,
                    nominalRp = nominalRp,
                    accountNumber = accountNumber,
                    accountName = accountName,
                    status = "Selesai"
                )
            )

            repository.addHistory(
                title = "Penarikan Sukses Rp $rpStr",
                amountRp = -koinRequired,
                category = "WITHDRAWAL"
            )

            _userMessage.value = "Penarikan Sukses Rp $rpStr ke $paymentMethod ($accountNumber)!"
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
