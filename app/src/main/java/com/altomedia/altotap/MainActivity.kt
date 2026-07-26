package com.altomedia.altotap

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.altomedia.altotap.ui.GameViewModel
import com.altomedia.altotap.ui.components.BannerAdView
import com.altomedia.altotap.ui.components.BottomNavBar
import com.altomedia.altotap.ui.components.TopHeader
import com.altomedia.altotap.ui.screens.HomeScreen
import com.altomedia.altotap.ui.screens.ProfileScreen
import com.altomedia.altotap.ui.screens.ReferralScreen
import com.altomedia.altotap.ui.screens.SpinWheelScreen
import com.altomedia.altotap.ui.screens.TasksScreen
import com.altomedia.altotap.ui.screens.UpgradeScreen
import com.altomedia.altotap.ui.screens.WithdrawalScreen
import com.altomedia.altotap.ui.theme.RupiahTapperTheme
import com.altomedia.altotap.util.AdMobManager

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.altomedia.altotap.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    AdMobManager.initialize(applicationContext)
    setContent {
      RupiahTapperTheme {
        var isSplashFinished by remember { mutableStateOf(false) }

        AnimatedContent(
            targetState = isSplashFinished,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "SplashTransition"
        ) { splashDone ->
          if (!splashDone) {
            SplashScreen(
                onLoadingComplete = { isSplashFinished = true }
            )
          } else {
            RupiahTapperApp()
          }
        }
      }
    }
  }
}

@Composable
fun RupiahTapperApp(
    viewModel: GameViewModel = viewModel()
) {
  val context = LocalContext.current
  val activity = context as? Activity

  val userStats by viewModel.userStats.collectAsStateWithLifecycle()
  val upgrades by viewModel.upgrades.collectAsStateWithLifecycle()
  val robots by viewModel.robots.collectAsStateWithLifecycle()
  val tasks by viewModel.tasks.collectAsStateWithLifecycle()
  val history by viewModel.history.collectAsStateWithLifecycle()
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val floatingTexts by viewModel.floatingTexts.collectAsStateWithLifecycle()
  val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()
  val spinTargetAngle by viewModel.spinTargetAngle.collectAsStateWithLifecycle()
  val pendingSpinResult by viewModel.pendingSpinResult.collectAsStateWithLifecycle()
  val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

  // Handle Toast feedback messages
  LaunchedEffect(userMessage) {
    userMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearMessage()
    }
  }

  val stats = userStats

  fun triggerRewardedAd(onRewardGranted: () -> Unit) {
    if (activity != null) {
      AdMobManager.showRewardedAd(activity, onRewardGranted)
    } else {
      onRewardGranted()
    }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
      // 1. Background Image Asset matching screenshots
      Image(
          painter = painterResource(id = R.drawable.img_game_bg),
          contentDescription = "Background",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
      )

      Column(
          modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .statusBarsPadding()
      ) {
        // 2. Top Header with Gift Box, Balance Pill, and Wallet
        TopHeader(
            balanceRp = stats?.balanceRp ?: 152500.0,
            energy = stats?.energy ?: 85,
            maxEnergy = stats?.maxEnergy ?: 100,
            onGiftClick = { viewModel.navigateTo(5) }, // Opens Spin Wheel
            onWalletClick = { viewModel.navigateTo(6) } // Opens Withdrawal
        )

        // 3. Screen Pages Switcher
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
          AnimatedContent(
              targetState = currentTab,
              transitionSpec = { fadeIn() togetherWith fadeOut() },
              label = "ScreenTransition"
          ) { pageIndex ->
            when (pageIndex) {
              0 -> HomeScreen(
                  userStats = stats,
                  floatingTexts = floatingTexts,
                  onTapCoin = { x, y -> viewModel.onTapCoin(x, y) },
                  onWatchAd = {
                    triggerRewardedAd {
                      viewModel.onAdClicked("Video Iklan Cuan")
                    }
                  }
              )
              1 -> TasksScreen(
                  tasks = tasks,
                  onClaimRewardWithAd = { taskId ->
                    triggerRewardedAd {
                      viewModel.claimTaskReward(taskId)
                    }
                  },
                  onWatchAdTask = { taskId ->
                    triggerRewardedAd {
                      viewModel.watchAdForTask(taskId)
                    }
                  },
                  onSocialTask = { taskId ->
                    val url = when (taskId) {
                      104 -> "https://youtube.com/@sidhanie"
                      105 -> "https://tiktok.com/@altomediaindonesia"
                      106 -> "https://instagram.com/sidhanie06"
                      else -> null
                    }
                    url?.let {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                      context.startActivity(intent)
                    }
                    viewModel.claimTaskReward(taskId)
                  }
              )
              2 -> UpgradeScreen(
                  upgrades = upgrades,
                  robots = robots,
                  history = history,
                  onBuyUpgrade = { id -> viewModel.buyUpgrade(id) },
                  onRentRobot = { robotId -> viewModel.rentRobot(robotId) }
              )
              3 -> ReferralScreen(
                  userStats = stats
              )
              4 -> ProfileScreen(
                  userStats = stats
              )
              5 -> SpinWheelScreen(
                  userStats = stats,
                  isSpinning = isSpinning,
                  spinTargetAngle = spinTargetAngle,
                  pendingSpinResult = pendingSpinResult,
                  onSpinClick = { viewModel.spinWheel() },
                  onWatchAdForFreeSpin = {
                    triggerRewardedAd {
                      viewModel.watchAdForSpin()
                    }
                  },
                  onClaimRewardWithAd = {
                    triggerRewardedAd {
                      viewModel.claimSpinReward()
                    }
                  },
                  onDismissDialog = {
                    viewModel.clearPendingSpinResult()
                  }
              )
              6 -> WithdrawalScreen(
                  userStats = stats,
                  onSubmitWithdrawal = { method, amount, number, name ->
                    viewModel.submitWithdrawal(method, amount, number, name)
                  }
              )
            }
          }
        }

        // 3.5 Banner Ad Container directly above Bottom Navigation Menu
        BannerAdView(
            onAdClick = { ad ->
                triggerRewardedAd {
                  viewModel.onAdClicked(ad.title)
                }
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // 4. Custom Circular Bottom Navigation Bar
        val selectedNavTab = if (currentTab in 0..4) currentTab else 0
        BottomNavBar(
            selectedTab = selectedNavTab,
            onTabSelected = { tab -> viewModel.navigateTo(tab) }
        )
      }
    }
  }
}
