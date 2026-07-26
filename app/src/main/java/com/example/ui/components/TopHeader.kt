package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GameGoldDark
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenDark
import com.example.ui.theme.GameGreenLight
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TopHeader(
    balanceRp: Double,
    energy: Int,
    maxEnergy: Int,
    onGiftClick: () -> Unit,
    onWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val formattedBalance = formatter.format(balanceRp.toLong())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Gift Box Button (Opens Spin Wheel Page 6)
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFFFF5252), Color(0xFFC62828))
                    )
                )
                .border(2.dp, GameGoldPrimary, CircleShape)
                .clickable { onGiftClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Roda Hadiah",
                tint = GameGoldPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        // Center: Balance & Energy Glossy Pill Container
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
                .height(48.dp)
                .shadow(6.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF073B13), Color(0xFF03220A))
                    )
                )
                .border(2.dp, GameGoldPrimary, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$formattedBalance KOIN",
                    color = GameGoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "  |  ",
                    color = Color.LightGray.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Energy",
                        tint = Color(0xFFFFEB3B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$energy/$maxEnergy",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Right: Wallet Button (Opens Penarikan Page 7)
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF8D6E63), Color(0xFF4E342E))
                    )
                )
                .border(2.dp, GameGoldPrimary, CircleShape)
                .clickable { onWalletClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = "Penarikan Wallet",
                tint = GameGoldPrimary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
