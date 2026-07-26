package com.altomedia.altotap.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altomedia.altotap.R
import com.altomedia.altotap.data.UserStatsEntity
import com.altomedia.altotap.ui.TapFloatingText
import com.altomedia.altotap.ui.theme.GameGoldDark
import com.altomedia.altotap.ui.theme.GameGoldPrimary
import com.altomedia.altotap.ui.theme.GameGreenCard
import com.altomedia.altotap.ui.theme.GameGreenCardDark
import com.altomedia.altotap.ui.theme.GameTextDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val id: Long,
    val angle: Double,
    val speed: Float,
    val size: Float,
    val color: Color,
    val distance: Animatable<Float, *>,
    val alpha: Animatable<Float, *>
)

@Composable
fun HomeScreen(
    userStats: UserStatsEntity?,
    floatingTexts: List<TapFloatingText>,
    onTapCoin: (Float, Float) -> Unit,
    onWatchAd: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val stats = userStats ?: UserStatsEntity()
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Coin press scale spring animation
    val coinScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CoinScale"
    )

    // Coin shake/rotation effect
    var coinRotation by remember { mutableStateOf(0f) }

    // Particle burst state
    val particles = remember { mutableStateListOf<Particle>() }

    fun triggerTapEffects() {
        // 1. Haptic Feedback Vibration
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

        // 2. Coin Tilt Shake
        scope.launch {
            val tiltDirection = if (Random.nextBoolean()) 8f else -8f
            coinRotation = tiltDirection
            delay(50)
            coinRotation = -tiltDirection * 0.5f
            delay(50)
            coinRotation = 0f
        }

        // 3. Particle Burst Generation
        val particleColors = listOf(
            GameGoldPrimary,
            Color(0xFFFFF176),
            Color(0xFFFFD54F),
            Color(0xFFFFB300),
            Color.White
        )

        val particleCount = 12
        for (i in 0 until particleCount) {
            val pId = System.nanoTime() + i
            val angle = (2 * Math.PI / particleCount) * i + Random.nextDouble(-0.2, 0.2)
            val speed = Random.nextFloat() * 120f + 60f
            val pSize = Random.nextFloat() * 8f + 6f
            val color = particleColors.random()

            val dist = Animatable(0f)
            val alp = Animatable(1f)

            val p = Particle(pId, angle, speed, pSize, color, dist, alp)
            particles.add(p)

            scope.launch {
                launch {
                    dist.animateTo(
                        targetValue = speed,
                        animationSpec = tween(durationMillis = 450)
                    )
                }
                launch {
                    alp.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 450)
                    )
                }
                delay(460)
                particles.remove(p)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Main Tapper Layout Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Giant Rp Coin Tapper Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Outer Glow Rings
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    GameGoldPrimary.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Outer Gold Ring Frame with Scale and Shake Rotation
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .scale(coinScale)
                        .rotate(coinRotation)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(GameGoldPrimary, GameGoldDark)
                            )
                        )
                        .border(6.dp, Color(0xFFFFF176), CircleShape)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (stats.energy > 0) {
                                triggerTapEffects()
                            }
                            onTapCoin(0f, 0f)
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner Green Coin Texture
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF1B8A2F), Color(0xFF093E13))
                                )
                            )
                            .border(4.dp, GameGoldPrimary.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // "Rp" Embossed Text Emblem
                        Text(
                            text = "Rp",
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            color = GameGoldPrimary,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    offset = androidx.compose.ui.geometry.Offset(4f, 6f),
                                    blurRadius = 8f
                                )
                            )
                        )
                    }
                }

                // Render Burst Particles Canvas around Coin
                Canvas(
                    modifier = Modifier
                        .size(320.dp)
                ) {
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    particles.forEach { p ->
                        val currentDist = p.distance.value
                        val currentAlpha = p.alpha.value
                        val px = centerOffset.x + (cos(p.angle) * currentDist).toFloat()
                        val py = centerOffset.y + (sin(p.angle) * currentDist).toFloat()

                        drawCircle(
                            color = p.color.copy(alpha = currentAlpha.coerceIn(0f, 1f)),
                            radius = p.size,
                            center = Offset(px, py)
                        )
                    }
                }

                // Render Floating Tap Text Popups
                floatingTexts.forEach { floatItem ->
                    Text(
                        text = floatItem.text,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GameGoldPrimary,
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    floatItem.offsetX.roundToInt(),
                                    (floatItem.offsetY - 120f).roundToInt()
                                )
                            }
                    )
                }
            }

            // Nonton Iklan Booster Callout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFFF8F00), Color(0xFFFFB300), Color(0xFFFFD54F))
                        )
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(14.dp))
                    .clickable { onWatchAd() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎬 NONTON IKLAN CUAN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF3E2723)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF3E2723))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+1.500 KOIN & +100 Energi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GameGoldPrimary
                        )
                    }
                }
            }

            // Bottom Green Box Container matching Image 1
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(96.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(GameGreenCard)
                    .border(2.dp, GameGoldPrimary, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Energi",
                                tint = GameTextDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = " Energi Tersisa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameTextDark
                            )
                        }

                        Text(
                            text = "${stats.energy} / ${stats.maxEnergy}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = GameTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    val progress = (stats.energy.toFloat() / stats.maxEnergy.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GameGreenCardDark,
                        trackColor = Color.White.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daya Ketuk: ${stats.pointsPerTap} KOIN/ketuk",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GameTextDark
                        )
                        Text(
                            text = "Total Ketuk: ${stats.totalTaps}x",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GameTextDark
                        )
                    }
                }
            }
        }
    }
}
