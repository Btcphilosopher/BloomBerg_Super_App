package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EconomicIndicator
import com.example.ui.theme.*

@Composable
fun EconomicsMacroScreen(
    indicators: List<EconomicIndicator>,
    onSelectTicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Macro Dashboard Hero Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GLOBAL ECONOMY COMMAND",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TerminalAmber
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hero Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("US CPI", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("3.1%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalTextPrimary)
                        }
                        Column {
                            Text("UK CPI", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("2.4%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalTextPrimary)
                        }
                        Column {
                            Text("EU CPI", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("2.0%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalTextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("US GDP", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("2.4%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalGreen)
                        }
                        Column {
                            Text("UK GDP", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("1.3%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalGreen)
                        }
                        Column {
                            Text("FED FUNDS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("4.25%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalAmberLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("BOE RATE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("3.75%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalAmberLight)
                        }
                        Column {
                            Text("ECB RATE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("2.50%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalAmberLight)
                        }
                        Column {
                            Text("BOJ RATE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("0.50%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalCyan)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "ECONOMIC RELEASES & CENTRAL BANK CALENDAR",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TerminalTextPrimary
            )
        }

        items(indicators) { ind ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = TerminalSurfaceVariant,
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    text = ind.country,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = TerminalAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(ind.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        }
                        Text("Release: ${ind.releaseDate} • Unit: ${ind.unit}", fontSize = 11.sp, color = TerminalTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(ind.currentValue, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TerminalAmberLight)
                        Text("Prev: ${ind.previousValue} | Fcst: ${ind.forecast}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                    }
                }
            }
        }
    }
}
