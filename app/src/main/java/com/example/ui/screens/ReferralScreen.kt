package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStatsEntity
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameTextDark
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ReferralScreen(
    userStats: UserStatsEntity?,
    modifier: Modifier = Modifier
) {
    val stats = userStats ?: UserStatsEntity()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    val referralCode = stats.userId
    val referralLink = "https://play.google.com/store/apps/details?id=com.altomedia.altotap&referrer=$referralCode"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section 1: REFERRAL & PLAY STORE TAUTAN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGreenCardDark, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KODE & TAUTAN REFERENSI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GameTextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 6-Digit Referral Code Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GameGreenCardDark)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KODE REFERRAL (6 DIGIT)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GameGoldPrimary
                            )
                            Text(
                                text = referralCode,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Kode",
                            tint = GameGoldPrimary,
                            modifier = Modifier
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(referralCode))
                                    Toast.makeText(context, "Kode referral ($referralCode) tersalin!", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Play Store Referral Link Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(GameGreenCardDark)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = referralLink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Salin Tautan Play Store",
                            tint = GameGoldPrimary,
                            modifier = Modifier
                                .clickable {
                                    clipboardManager.setText(AnnotatedString(referralLink))
                                    Toast.makeText(context, "Tautan Play Store tersalin!", Toast.LENGTH_SHORT).show()
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Yellow BAGIKAN Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(GameGoldPrimary)
                        .clickable {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Ayo mainkan Rupiah Tapper & kumpulkan koin gratis! Masukkan Kode Referral saya: $referralCode atau unduh langsung di Play Store: $referralLink"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Kode & Tautan Referral"))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = GameTextDark
                        )
                        Text(
                            text = "  BAGIKAN REFERRAL",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = GameTextDark
                        )
                    }
                }
            }
        }

        // Section 2: Summary Stats Row (2 Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(GameGreenCard)
                    .border(2.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Teman Diundang",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${stats.invitedFriends} Orang",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGreenCardDark
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(GameGreenCard)
                    .border(2.dp, GameGreenCardDark, RoundedCornerShape(14.dp))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Total Komisi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameTextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val commissionKoin = stats.invitedFriends * 5000.0
                    Text(
                        text = "${formatter.format(commissionKoin.toLong())} KOIN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGreenCardDark
                    )
                }
            }
        }

        // Section 3: CARA KERJA Box Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGreenCardDark, RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CARA KERJA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GameTextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GuideStepItem(
                        number = "1",
                        text = "Bagikan tautan referensi unik milikmu kepada teman atau media sosial."
                    )
                    GuideStepItem(
                        number = "2",
                        text = "Ajak teman untuk mendaftar dan aktif mengetuk koin di aplikasi."
                    )
                    GuideStepItem(
                        number = "3",
                        text = "Dapatkan hadiah instan 5.000 KOIN + komisi 10% dari setiap ketukan koin temanmu!"
                    )
                }
            }
        }
    }
}

@Composable
fun GuideStepItem(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GameGreenCardDark)
            .border(1.dp, GameGoldPrimary.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(GameGoldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = GameTextDark
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}
