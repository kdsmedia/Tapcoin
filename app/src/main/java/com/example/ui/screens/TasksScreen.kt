package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskEntity
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameTextDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TasksScreen(
    tasks: List<TaskEntity>,
    onClaimRewardWithAd: (Int) -> Unit,
    onWatchAdTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val dailyTasks = tasks.filter { it.isDaily }
    val mandatoryTasks = tasks.filter { !it.isDaily }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Section 1: TUGAS HARIAN
        SectionBannerHeader(title = "TUGAS HARIAN")

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1.3f)
        ) {
            items(dailyTasks) { task ->
                TaskCardItem(
                    task = task,
                    formatter = formatter,
                    onClaimClick = {
                        if (!task.isCompleted) {
                            if (task.progress >= task.maxProgress) {
                                onClaimRewardWithAd(task.id)
                            } else if (task.id == 4 || task.title.contains("Tonton", ignoreCase = true) || task.id == 2) {
                                onWatchAdTask(task.id)
                            } else {
                                onClaimRewardWithAd(task.id)
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section 2: TUGAS WAJIB
        SectionBannerHeader(title = "TUGAS WAJIB")

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(mandatoryTasks) { task ->
                TaskCardItem(
                    task = task,
                    formatter = formatter,
                    onClaimClick = {
                        if (!task.isCompleted) {
                            onClaimRewardWithAd(task.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TaskCardItem(
    task: TaskEntity,
    formatter: NumberFormat,
    onClaimClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(GameGreenCard)
            .border(2.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GameTextDark,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Text(
                text = "+${formatter.format(task.rewardRp.toLong())} KOIN",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GameGreenCardDark
            )

            // Progress Bar
            val progFraction = (task.progress.toFloat() / task.maxProgress.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = { progFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GameGreenCardDark,
                trackColor = Color.White.copy(alpha = 0.6f),
                strokeCap = StrokeCap.Round
            )

            // Claim / Action Button
            val isWatchTask = task.id == 4 || task.title.contains("Tonton", ignoreCase = true)
            val isCheckinTask = task.id == 2

            val buttonBg = when {
                task.isCompleted -> Color(0xFF2E7D32)
                task.progress >= task.maxProgress -> GameGoldPrimary
                isWatchTask || isCheckinTask -> Color(0xFF1976D2) // Blue for Ad action
                else -> GameGreenCardDark
            }

            val buttonText = when {
                task.isCompleted -> "SELESAI"
                task.progress >= task.maxProgress -> "🎬 KLAIM"
                isWatchTask -> "🎬 TONTON (${task.progress}/${task.maxProgress})"
                isCheckinTask -> "🎬 CHECK-IN"
                else -> "${task.progress}/${task.maxProgress}"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(buttonBg)
                    .clickable { onClaimClick() }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (task.progress >= task.maxProgress && !task.isCompleted) GameTextDark else Color.White
                )
            }
        }
    }
}
