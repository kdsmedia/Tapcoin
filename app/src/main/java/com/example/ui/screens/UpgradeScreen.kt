package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.HistoryEntity
import com.example.data.RobotEntity
import com.example.data.UpgradeEntity
import com.example.ui.theme.GameGoldDark
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameTextDark
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UpgradeScreen(
    upgrades: List<UpgradeEntity>,
    robots: List<RobotEntity>,
    history: List<HistoryEntity>,
    onBuyUpgrade: (String) -> Unit,
    onRentRobot: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section 1: UPGRADE SISTEM
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionBannerHeader(title = "UPGRADE SISTEM")

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val multiplierUpgrade = upgrades.find { it.id == "multiplier" }
                val maxEnergyUpgrade = upgrades.find { it.id == "max_energy" }
                val rechargeUpgrade = upgrades.find { it.id == "recharge_speed" }

                val multLevel = multiplierUpgrade?.currentLevel ?: 1
                val multMax = multiplierUpgrade?.maxLevel ?: 50
                val multPriceText = if (multLevel >= multMax) "MAX" else "${formatter.format(multiplierUpgrade?.costRp?.toLong() ?: 2500)} KOIN"

                val maxEnergyLevel = maxEnergyUpgrade?.currentLevel ?: 1
                val maxEnergyMax = maxEnergyUpgrade?.maxLevel ?: 50
                val maxEnergyPriceText = if (maxEnergyLevel >= maxEnergyMax) "MAX" else "${formatter.format(maxEnergyUpgrade?.costRp?.toLong() ?: 5000)} KOIN"

                val rechargeLevel = rechargeUpgrade?.currentLevel ?: 1
                val rechargeMax = rechargeUpgrade?.maxLevel ?: 50
                val rechargePriceText = if (rechargeLevel >= rechargeMax) "MAX" else "${formatter.format(rechargeUpgrade?.costRp?.toLong() ?: 7500)} KOIN"

                // Card 1: Tap Power
                UpgradeItemCard(
                    modifier = Modifier.weight(1f),
                    title = "Daya Ketuk",
                    levelText = "Lv. $multLevel",
                    priceText = multPriceText,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E88E5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = "x2",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    onClick = { onBuyUpgrade("multiplier") }
                )

                // Card 2: Heart (Max Energy)
                UpgradeItemCard(
                    modifier = Modifier.weight(1f),
                    title = "Kapasitas",
                    levelText = "Lv. $maxEnergyLevel",
                    priceText = maxEnergyPriceText,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE53935)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Heart",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    onClick = { onBuyUpgrade("max_energy") }
                )

                // Card 3: Battery (Recharge)
                UpgradeItemCard(
                    modifier = Modifier.weight(1f),
                    title = "Isi Baterai",
                    levelText = "Lv. $rechargeLevel",
                    priceText = rechargePriceText,
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = "Battery",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    onClick = { onBuyUpgrade("recharge_speed") }
                )
            }
        }

        // Section 2: SEWA ROBOT
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionBannerHeader(title = "SEWA ROBOT")

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                robots.take(3).forEach { robot ->
                    RobotCardItem(
                        modifier = Modifier.weight(1f),
                        robot = robot,
                        formatter = formatter,
                        onRentClick = { onRentRobot(robot.id) }
                    )
                }
            }
        }

        // Section 3: RIWAYAT (Fixed height container with internal LazyColumn)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            SectionBannerHeader(title = "RIWAYAT")

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(GameGreenCard)
                    .border(2.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
                    .padding(8.dp)
            ) {
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada riwayat aktivitas",
                            color = GameTextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(history) { logItem ->
                            val dateStr = SimpleDateFormat("HH:mm - dd MMM", Locale("id", "ID"))
                                .format(Date(logItem.timestamp))
                            val isPositive = logItem.amountRp >= 0
                            val amountFormatted = if (logItem.category == "WITHDRAWAL") {
                                val rpVal = (-logItem.amountRp / 1000.0).toLong()
                                "-Rp ${formatter.format(rpVal)}"
                            } else {
                                if (isPositive) {
                                    "+${formatter.format(logItem.amountRp.toLong())} KOIN"
                                } else {
                                    "-${formatter.format(-logItem.amountRp.toLong())} KOIN"
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GameGreenCardDark)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = logItem.title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = dateStr,
                                        color = Color.LightGray,
                                        fontSize = 10.sp
                                    )
                                }

                                Text(
                                    text = amountFormatted,
                                    color = if (isPositive) GameGoldPrimary else Color(0xFFFF8A80),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionBannerHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(GameGreenCard, Color(0xFF90CE38))
                )
            )
            .border(2.dp, GameGoldPrimary, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            color = GameTextDark,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UpgradeItemCard(
    title: String,
    levelText: String,
    priceText: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(GameGreenCard)
            .border(2.dp, GameGreenCardDark, RoundedCornerShape(12.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GameTextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = levelText,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = GameTextDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(GameGreenCardDark)
                    .clickable { onClick() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = priceText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GameGoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RobotCardItem(
    robot: RobotEntity,
    formatter: NumberFormat,
    onRentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = when {
        robot.name.contains("Auto-Tapper", ignoreCase = true) -> "Bot Pro"
        robot.name.contains("Cyber", ignoreCase = true) -> "Bot Cyber"
        robot.name.contains("Bot-v1", ignoreCase = true) || robot.name.contains("v1", ignoreCase = true) -> "Bot v1"
        else -> robot.name.replace("Robot ", "Bot ")
    }

    Box(
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(GameGreenCard)
            .border(2.dp, GameGreenCardDark, RoundedCornerShape(12.dp))
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_robot_avatar),
                contentDescription = displayName,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GameTextDark,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "+${robot.earningsPerSec.toInt()} KOIN/dtk",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = GameGreenCardDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (robot.isRented) Color(0xFF2E7D32) else GameGreenCardDark)
                    .clickable { if (!robot.isRented) onRentClick() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (robot.isRented) "AKTIF" else "${formatter.format(robot.rentPriceRp.toLong())} KOIN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (robot.isRented) Color.White else GameGoldPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
