package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStatsEntity
import com.example.ui.SpinResult
import com.example.ui.theme.GameGoldDark
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameTextDark

@Composable
fun SpinWheelScreen(
    userStats: UserStatsEntity?,
    isSpinning: Boolean,
    spinTargetAngle: Float,
    pendingSpinResult: SpinResult? = null,
    onSpinClick: () -> Unit,
    onWatchAdForFreeSpin: () -> Unit = {},
    onClaimRewardWithAd: () -> Unit = {},
    onDismissDialog: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats = userStats ?: UserStatsEntity()

    val animatedRotation by animateFloatAsState(
        targetValue = spinTargetAngle,
        animationSpec = tween(
            durationMillis = 3500,
            easing = FastOutSlowInEasing
        ),
        label = "SpinWheelRotation"
    )

    if (pendingSpinResult != null) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = GameGreenCard,
            titleContentColor = GameGoldPrimary,
            textContentColor = Color.White,
            title = {
                Text(
                    text = "HADIAH YANG DIDAPAT",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (pendingSpinResult.isZonk) "ZONK" else pendingSpinResult.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGoldPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (!pendingSpinResult.isZonk) {
                                onClaimRewardWithAd()
                            } else {
                                onDismissDialog()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GameGoldPrimary),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text(
                            text = "KLAIM",
                            color = GameTextDark,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Outer Green Container matching Image 6
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGreenCardDark, RoundedCornerShape(16.dp))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Wheel Pointer Diamond Arrow
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Wheel Pointer",
                    tint = Color.Red,
                    modifier = Modifier.size(52.dp)
                )

                // Wheel Canvas Container
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .shadow(12.dp, CircleShape)
                        .clip(CircleShape)
                        .background(GameGoldPrimary)
                        .border(6.dp, GameGoldDark, CircleShape)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating Disc Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(animatedRotation)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        val radius = canvasWidth / 2f

                        val segmentColors = listOf(
                            Color(0xFFEC407A), // Pink: Rp.250
                            Color(0xFF66BB6A), // Light Green: Rp.100
                            Color(0xFFFFA726), // Orange: 200 KOIN
                            Color(0xFF29B6F6), // Blue: 50 KOIN
                            Color(0xFFAB47BC), // Purple: Rp.50
                            Color(0xFFFF7043), // Dark Orange: 250 KOIN
                            Color(0xFF26A69A), // Teal: 500 KOIN
                            Color(0xFFEF5350)  // Red: ZONK
                        )

                        val segmentLabels = listOf(
                            "250 KOIN",
                            "100 KOIN",
                            "200 KOIN",
                            "50 KOIN",
                            "150 KOIN",
                            "250 KOIN",
                            "500 KOIN",
                            "ZONK"
                        )

                        val sweepAngle = 360f / 8f

                        for (i in 0 until 8) {
                            val startAngle = i * sweepAngle
                            drawArc(
                                color = segmentColors[i],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(canvasWidth, canvasHeight)
                            )

                            // Text Label Drawing
                            drawContext.canvas.nativeCanvas.apply {
                                val textAngle = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
                                val textRadius = radius * 0.65f
                                val x = (radius + textRadius * Math.cos(textAngle)).toFloat()
                                val y = (radius + textRadius * Math.sin(textAngle)).toFloat()

                                val paint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.WHITE
                                    textSize = 28f
                                    isFakeBoldText = true
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                save()
                                rotate((startAngle + sweepAngle / 2f + 90f), x, y)
                                drawText(segmentLabels[i], x, y, paint)
                                restore()
                            }
                        }

                        // Center Gold Ring
                        drawCircle(
                            color = Color(0xFFFFD700),
                            radius = radius * 0.22f,
                            center = Offset(radius, radius)
                        )
                        drawCircle(
                            color = Color(0xFFB8860B),
                            radius = radius * 0.18f,
                            center = Offset(radius, radius)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Kesempatan Spin Gratis: ${stats.spinAttemptsLeft} Kali",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameGoldPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Big Green PUTAR Button matching Image 6
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .height(50.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSpinning) Color.Gray else GameGreenCardDark)
                        .border(2.dp, GameGoldPrimary, RoundedCornerShape(14.dp))
                        .clickable { if (!isSpinning) onSpinClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSpinning) "MEMUTAR..." else "PUTAR RODA",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Button Ads: 3x kesempatan Iklan untuk mendapatkan +1 Spin Gratis
                val canWatchAd = stats.spinAdsRemaining > 0 && !isSpinning
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(48.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (canWatchAd) com.example.ui.theme.GameYellowButton else Color.Gray.copy(alpha = 0.5f)
                        )
                        .border(
                            2.dp,
                            if (canWatchAd) GameGoldPrimary else Color.LightGray,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = canWatchAd) { onWatchAdForFreeSpin() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (stats.spinAdsRemaining > 0)
                                "🎬 TONTON IKLAN (+1 SPIN) [Sisa: ${stats.spinAdsRemaining}/3]"
                            else
                                "🎬 BATAS IKLAN SPIN HABIS (0/3)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (canWatchAd) GameTextDark else Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
