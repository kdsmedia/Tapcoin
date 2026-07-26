package com.example.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.GameGoldPrimary
import com.example.ui.theme.GameGreenCard
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

data class AdBannerItem(
    val title: String = "Google AdMob Banner",
    val subtitle: String = "",
    val ctaText: String = "",
    val badgeText: String = "",
    val adUnitId: String = "ca-app-pub-6881903056221433/6767996974"
)

@Composable
fun BannerAdView(
    onAdClick: (AdBannerItem) -> Unit = {},
    adUnitId: String = "ca-app-pub-6881903056221433/6767996974",
    modifier: Modifier = Modifier
) {
    // Google AdMob Official Test Banner Unit ID as fallback if production unit returns no-fill
    val testAdUnitId = "ca-app-pub-3940256099942544/6300978111"
    var currentUnitId by remember { mutableStateOf(adUnitId) }
    var adFailedBoth by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(GameGreenCard)
            .border(1.5.dp, GameGoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (!adFailedBoth) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context.applicationContext).apply {
                        setAdSize(AdSize.BANNER)
                        this.adUnitId = currentUnitId
                        adListener = object : AdListener() {
                            override fun onAdClicked() {
                                super.onAdClicked()
                                onAdClick(AdBannerItem(adUnitId = currentUnitId))
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                super.onAdFailedToLoad(error)
                                Log.e("BannerAdView", "Banner ad ($currentUnitId) failed to load: ${error.message} (code: ${error.code})")
                                if (currentUnitId != testAdUnitId) {
                                    // Retry with official test ad unit ID so banner displays in test environment
                                    currentUnitId = testAdUnitId
                                } else {
                                    adFailedBoth = true
                                }
                            }

                            override fun onAdLoaded() {
                                super.onAdLoaded()
                                Log.d("BannerAdView", "Banner ad ($currentUnitId) loaded successfully")
                            }
                        }
                        loadAd(AdRequest.Builder().build())
                    }
                },
                update = { view ->
                    if (view.adUnitId != currentUnitId) {
                        view.adUnitId = currentUnitId
                        view.loadAd(AdRequest.Builder().build())
                    }
                }
            )
        } else {
            // Interactive fallback banner if network or AdMob servers are unreachable
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAdClick(AdBannerItem(title = "Iklan Cuan", adUnitId = currentUnitId)) }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Ad",
                    tint = GameGoldPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "📢 Tonton Iklan Banner untuk Bonus Rp 2.500",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}



