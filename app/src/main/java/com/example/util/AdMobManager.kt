package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-6881903056221433/8191925130"

    private var rewardedAd: RewardedAd? = null
    private var isLoading = false

    fun initialize(context: Context) {
        try {
            val appContext = context.applicationContext
            MobileAds.initialize(appContext) {
                preloadRewardedAd(appContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "MobileAds initialize error", e)
        }
    }

    fun preloadRewardedAd(context: Context) {
        if (rewardedAd != null || isLoading) return
        isLoading = true

        val appContext = context.applicationContext

        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            appContext,
            REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${adError.message}")
                    rewardedAd = null
                    isLoading = false
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded successfully.")
                    rewardedAd = ad
                    isLoading = false
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onRewardGranted: () -> Unit) {
        val currentAd = rewardedAd
        if (currentAd != null) {
            rewardedAd = null // consume ad
            var rewardEarned = false

            currentAd.show(activity) { _ ->
                rewardEarned = true
                onRewardGranted()
            }

            preloadRewardedAd(activity)
        } else {
            // Fallback reward if ad is still loading or unavailable in dev environment
            onRewardGranted()
            preloadRewardedAd(activity)
        }
    }
}
