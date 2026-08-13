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
import com.example.data.model.FixedIncomeBond
import com.example.data.model.PricePoint
import com.example.data.model.YieldPoint
import com.example.ui.components.FinancialCanvasChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChartType

@Composable
fun FixedIncomeScreen(
    yieldPoints: List<YieldPoint>,
    corporateBonds: List<FixedIncomeBond>,
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
        // Government Yield Curve Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "US TREASURY YIELD CURVE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TerminalAmber
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val pricePoints = yieldPoints.mapIndexed { idx, yp ->
                        PricePoint(idx.toLong(), yp.yieldPct, yp.yieldPct, yp.yieldPct, yp.yieldPct, 0)
                    }

                    FinancialCanvasChart(
                        prices = pricePoints,
                        chartType = ChartType.YIELD_CURVE,
                        showSMA = false,
                        showRSI = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        yieldPoints.forEach { yp ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(yp.maturityLabel, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                                Text("${yp.yieldPct}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TerminalCyan)
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "CORPORATE CREDIT & HIGH YIELD BONDS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TerminalTextPrimary
            )
        }

        items(corporateBonds) { bond ->
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
                                    text = bond.rating,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = TerminalAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(bond.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        }
                        Text("Maturity: ${bond.maturity} • Duration: ${bond.durationYears} yrs", fontSize = 11.sp, color = TerminalTextSecondary)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("YTM ${bond.yieldToMaturity}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalGreen)
                        Text("Spread: +${bond.spreadBps} bps | Px: ${bond.price}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                    }
                }
            }
        }
    }
}
