package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStatsEntity
import com.example.ui.theme.GameGoldDark
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameTextDark

@Composable
fun ProfileScreen(
    userStats: UserStatsEntity?,
    modifier: Modifier = Modifier
) {
    val stats = userStats ?: UserStatsEntity()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var activeDialogTitle by remember { mutableStateOf<String?>(null) }
    var activeDialogContent by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(6.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gold User Avatar Badge with VIP Tag
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(GameGoldPrimary)
                            .border(3.dp, GameGoldDark, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(GameGreenCardDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "User Avatar",
                                tint = GameGoldPrimary,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GameGoldPrimary)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "PRO TAPPER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GameTextDark
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ID: ${stats.userId}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = GameTextDark,
                                modifier = Modifier.weight(1f)
                            )

                            // Copy ID Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GameGreenCardDark)
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(stats.userId))
                                        Toast.makeText(context, "ID Akun berhasil disalin!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy ID",
                                        tint = GameGoldPrimary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "SALIN",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GameGoldPrimary
                                    )
                                }
                            }
                        }

                        Text(
                            text = stats.userEmail,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GameTextDark.copy(alpha = 0.85f)
                        )
                    }
                }

                // Level / Performance summary bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GameGreenCardDark)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STATUS AKUN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GameGoldPrimary
                        )
                        Text(
                            text = "Aktif & Terverifikasi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GameGoldPrimary.copy(alpha = 0.2f))
                            .border(1.dp, GameGoldPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Level ${stats.tapMultiplierLevel}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = GameGoldPrimary
                        )
                    }
                }
            }
        }

        // Section 1: Statistik Permainan
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = GameGoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Statistik Permainan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GameTextDark
                )
            }

            // Grid 2x2 for stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Default.TouchApp,
                    label = "Total Ketukan",
                    value = "${stats.totalTaps}x",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Default.Star,
                    label = "Poin / Ketuk",
                    value = "${stats.pointsPerTap} KOIN",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Default.CheckCircle,
                    label = "Tugas Selesai",
                    value = "${stats.completedTasks} Tugas",
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Default.Share,
                    label = "Teman Diundang",
                    value = "${stats.invitedFriends} Orang",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section 2: Pusat Informasi & Bantuan
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = GameGoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Informasi & Bantuan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GameTextDark
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "Tentang Aplikasi",
                    subtitle = "Versi 1.0 • Rupiah Tapper",
                    onClick = {
                        activeDialogTitle = "Tentang Aplikasi"
                        activeDialogContent = "Rupiah Tapper v1.0\n\nAplikasi game tap-to-earn resmi karya anak bangsa. Ketuk koin, selesaikan tugas harian, dan tarik saldo ke E-Wallet DANA/OVO/GOPAY secara langsung!"
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Email,
                    title = "Hubungi Kami (Customer Support)",
                    subtitle = "Layanan bantuan & CS 24/7",
                    onClick = {
                        activeDialogTitle = "Hubungi Kami"
                        activeDialogContent = "Layanan Pelanggan Customer Care:\n• Email: altomediaindonesia@gmail.com\n\nJam Operasional: 24/7"
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Pernyataan Penolakan (Disclaimer)",
                    subtitle = "Aturan & syarat permainan",
                    onClick = {
                        activeDialogTitle = "Pernyataan Penolakan (Disclaimer)"
                        activeDialogContent = "Aplikasi ini adalah media permainan ketuk koin. Semua saldo dan komisi penarikan diproses sesuai dengan syarat dan ketentuan berlakunya permainan."
                    }
                )

                ProfileMenuItem(
                    icon = Icons.Default.Lock,
                    title = "Kebijakan Privasi",
                    subtitle = "Perlindungan data akun pengguna",
                    onClick = {
                        activeDialogTitle = "Kebijakan Privasi"
                        activeDialogContent = "Kami menghargai privasi Anda. Data akun (ID & Email) disimpan secara aman secara lokal dan tidak diberikan kepada pihak ketiga."
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Info Popup Dialog
    if (activeDialogTitle != null && activeDialogContent != null) {
        AlertDialog(
            onDismissRequest = {
                activeDialogTitle = null
                activeDialogContent = null
            },
            title = {
                Text(
                    text = activeDialogTitle!!,
                    fontWeight = FontWeight.Bold,
                    color = GameGoldPrimary
                )
            },
            text = {
                Text(
                    text = activeDialogContent!!,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (activeDialogTitle == "Hubungi Kami") {
                        TextButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:altomediaindonesia@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Dukungan Pelanggan Rupiah Tapper")
                                    }
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Email client tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text("KIRIM EMAIL", color = GameGoldPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    TextButton(
                        onClick = {
                            activeDialogTitle = null
                            activeDialogContent = null
                        }
                    ) {
                        Text("TUTUP", color = GameGoldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            containerColor = GameGreenCardDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun ProfileStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(GameGreenCard)
            .border(1.5.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GameGreenCardDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GameGoldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameTextDark.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }

            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = GameTextDark
            )
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(GameGreenCard)
            .border(1.5.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GameGreenCardDark),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = GameGoldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GameTextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = GameTextDark.copy(alpha = 0.75f)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = GameGoldPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
