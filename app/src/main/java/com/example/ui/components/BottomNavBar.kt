package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GameGoldDark
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCardDark
import com.example.ui.theme.GameGreenLight

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavIconButton(
            icon = Icons.Default.Home,
            contentDescription = "Beranda",
            isSelected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )

        NavIconButton(
            icon = Icons.Default.Assignment,
            contentDescription = "Tugas",
            isSelected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )

        // Center Upgrade Button (Highlighted with Upward Green Arrow)
        NavIconButton(
            icon = Icons.Default.ArrowUpward,
            contentDescription = "Upgrade",
            isSelected = selectedTab == 2,
            isCenterBig = true,
            onClick = { onTabSelected(2) }
        )

        NavIconButton(
            icon = Icons.Default.GroupAdd,
            contentDescription = "Referral",
            isSelected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )

        NavIconButton(
            icon = Icons.Default.Person,
            contentDescription = "Profil",
            isSelected = selectedTab == 4,
            onClick = { onTabSelected(4) }
        )
    }
}

@Composable
private fun NavIconButton(
    icon: ImageVector,
    contentDescription: String,
    isSelected: Boolean,
    isCenterBig: Boolean = false,
    onClick: () -> Unit
) {
    val sizeDp = if (isCenterBig) 62.dp else 52.dp
    val iconSizeDp = if (isCenterBig) 32.dp else 26.dp
    val yOffset = if (isCenterBig) (-8).dp else 0.dp

    val backgroundBrush = if (isCenterBig) {
        Brush.verticalGradient(
            listOf(Color(0xFF88C236), Color(0xFF0C4D19))
        )
    } else {
        Brush.verticalGradient(
            listOf(GameGoldPrimary, GameGoldDark)
        )
    }

    val innerBgColor = if (isCenterBig) {
        GameGreenCardDark
    } else if (isSelected) {
        Color(0xFF0F5A1C)
    } else {
        Color(0xFF144D1E)
    }

    val iconTint = if (isCenterBig) {
        GameGoldPrimary
    } else if (isSelected) {
        GameGoldPrimary
    } else {
        Color(0xFFFFECB3)
    }

    Box(
        modifier = Modifier
            .offset(y = yOffset)
            .size(sizeDp)
            .shadow(if (isSelected) 10.dp else 4.dp, CircleShape)
            .clip(CircleShape)
            .background(backgroundBrush)
            .border(
                width = if (isSelected || isCenterBig) 3.dp else 1.5.dp,
                color = if (isSelected) Color.White else GameGoldPrimary,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(sizeDp - 8.dp)
                .clip(CircleShape)
                .background(innerBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconTint,
                modifier = Modifier.size(iconSizeDp)
            )
        }
    }
}
