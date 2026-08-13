package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.PortfolioAnalytics
import com.example.data.model.PortfolioHolding
import com.example.ui.theme.*

@Composable
fun PortfolioDashboardScreen(
    portfolio: PortfolioAnalytics,
    holdings: List<PortfolioHolding>,
    onSelectTicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val analytics = portfolio
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Portfolio Summary Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PORTFOLIO COMMAND",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TerminalAmber
                        )

                        Surface(
                            color = TerminalSurfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "RISK: ${analytics.riskLevel.uppercase()}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = TerminalCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${"%,.2f".format(analytics.totalValue)}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = TerminalAmberLight
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "+$${"%,.2f".format(analytics.dayChangeDollar)} (+${analytics.dayChangePct}%) today",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TerminalGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Divider(color = TerminalBorder)

                    Spacer(modifier = Modifier.height(10.dp))

                    // Asset Allocation Percentages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        analytics.assetAllocation.forEach { (asset, pct) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(asset.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                                Text("${(pct * 100).toInt()}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalTextPrimary)
                            }
                        }
                    }
                }
            }
        }

        // Section: SIMULATED QUANTITATIVE RISK ANALYTICS
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PORTFOLIO RISK & FACTOR ANALYTICS",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TerminalAmber
                    )

                    Text(
                        text = "Simulated metrics derived from historical multi-factor Barra risk model.",
                        fontSize = 10.sp,
                        color = TerminalTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("VOLATILITY", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                            Text("${analytics.volatilityPct}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        }
                        Column {
                            Text("BETA", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                            Text("${analytics.beta}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        }
                        Column {
                            Text("SHARPE RATIO", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                            Text("${analytics.sharpeRatio}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalGreen)
                        }
                        Column {
                            Text("MAX DRAWDOWN", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                            Text("${analytics.maxDrawdownPct}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalRed)
                        }
                        Column {
                            Text("95% VaR (1D)", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                            Text("$${"%,.0f".format(analytics.varSimulated95Pct)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmberLight)
                        }
                    }
                }
            }
        }

        // Section: POSITIONS TABLE
        item {
            Text(
                text = "PORTFOLIO HOLDINGS & POSITIONS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TerminalTextPrimary
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerminalSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ASSET", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1.2f))
                Text("VALUE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1f))
                Text("UNREAL P&L", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1f))
                Text("WEIGHT", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(0.7f))
            }
        }

        items(holdings) { pos ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTicker(pos.ticker) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(pos.ticker, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        Text("${pos.shares.toInt()} shares @ $${pos.avgCost}", fontSize = 11.sp, color = TerminalTextSecondary)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("$${"%,.0f".format(pos.totalValue)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                        Text("$${"%.2f".format(pos.currentPrice)}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalTextMuted)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        val isUp = pos.pnlDollar >= 0
                        Text("${if (isUp) "+" else ""}$${"%,.0f".format(pos.pnlDollar)}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isUp) TerminalGreen else TerminalRed)
                        Text("${if (isUp) "+" else ""}${pos.pnlPct}%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = if (isUp) TerminalGreen else TerminalRed)
                    }

                    Text("${pos.weightPct}%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalAmber, modifier = Modifier.weight(0.7f))
                }
            }
        }
    }
}
