package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
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
fun WithdrawalScreen(
    userStats: UserStatsEntity?,
    onSubmitWithdrawal: (paymentMethod: String, nominalRp: Double, accountNumber: String, accountName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = userStats ?: UserStatsEntity()
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

    var selectedMethod by remember { mutableStateOf("DANA") }
    var selectedNominal by remember { mutableStateOf(500.0) }
    var accountNumberInput by remember { mutableStateOf("081234567890") }
    var accountNameInput by remember { mutableStateOf("Sidhani Pratama") }

    val methods = listOf("DANA", "OVO", "GOPAY")
    val nominals = listOf(100.0, 200.0, 500.0, 1000.0, 2000.0, 5000.0)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section 1: Payment Method Selection Row matching Image 7
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            methods.forEach { method ->
                val isSelected = selectedMethod == method
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(GameGreenCardDark)
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) GameGoldPrimary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedMethod = method },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = method,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) GameGoldPrimary else Color.White
                    )
                }
            }
        }

        // Section 2: Nominal Grid matching Image 7
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGreenCardDark, RoundedCornerShape(16.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PILIH NOMINAL PENARIKAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = GameTextDark
                    )
                    Text(
                        text = "1.000 KOIN = Rp 1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GameGreenCardDark
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(118.dp)
                ) {
                    items(nominals) { nominal ->
                        val isSelected = selectedNominal == nominal
                        val koinRequired = (nominal * 1000).toLong()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .shadow(2.dp, RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .background(GameGreenCardDark)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) GameGoldPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedNominal = nominal },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Rp${formatter.format(nominal.toLong())}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) GameGoldPrimary else Color.White
                                )
                                Text(
                                    text = "${formatter.format(koinRequired)} KOIN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GameGoldPrimary.copy(alpha = 0.9f) else Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Input Fields Box matching Image 7
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "INFORMASI AKUN E-WALLET",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = GameTextDark
                    )

                    // Field 1: Account Number
                    OutlinedTextField(
                        value = accountNumberInput,
                        onValueChange = { accountNumberInput = it },
                        label = { Text("Nomor HP / Akun E-Wallet ($selectedMethod)", fontSize = 12.sp) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GameGreenCardDark,
                            unfocusedContainerColor = GameGreenCardDark,
                            focusedBorderColor = GameGoldPrimary,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = GameGoldPrimary,
                            unfocusedLabelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Field 2: Account Name
                    OutlinedTextField(
                        value = accountNameInput,
                        onValueChange = { accountNameInput = it },
                        label = { Text("Nama Pemilik Rekening / E-Wallet", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GameGreenCardDark,
                            unfocusedContainerColor = GameGreenCardDark,
                            focusedBorderColor = GameGoldPrimary,
                            unfocusedBorderColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = GameGoldPrimary,
                            unfocusedLabelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Yellow Action Button AJUKAN PENARIKAN matching Image 7
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(GameGoldPrimary)
                        .clickable {
                            onSubmitWithdrawal(
                                selectedMethod,
                                selectedNominal,
                                accountNumberInput,
                                accountNameInput
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AJUKAN PENARIKAN",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GameTextDark
                    )
                }
            }
        }

        // Bottom Box Container matching Image 7
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(GameGreenCard)
                .border(2.dp, GameGoldPrimary, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saldo Saat Ini:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameTextDark
                    )
                    Text(
                        text = "${formatter.format(stats.balanceRp.toLong())} KOIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GameGreenCardDark
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Nilai Penarikan:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GameTextDark
                    )
                    Text(
                        text = "Rp ${formatter.format((stats.balanceRp / 1000.0).toLong())}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GameTextDark
                    )
                }
            }
        }
    }
}
