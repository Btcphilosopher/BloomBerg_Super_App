package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketItem
import com.example.data.model.NewsArticle
import com.example.data.model.PortfolioAnalytics
import com.example.data.model.PricePoint
import com.example.ui.components.FinancialCanvasChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChartType

@Composable
fun TerminalWorkspaceScreen(
    marketItems: List<MarketItem>,
    news: List<NewsArticle>,
    portfolio: PortfolioAnalytics,
    chartPrices: List<PricePoint>,
    chartType: ChartType,
    onSelectTicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(6.dp)
    ) {
        // Workspace Grid Header Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurface)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLOOMBERG WORKSPACE [DESKTOP 2x2 MULTI-PANEL]",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TerminalAmber
            )

            Surface(
                color = TerminalAmber,
                shape = RoundedCornerShape(2.dp)
            ) {
                Text(
                    text = "LAYOUT: 2x2 ACTIVE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2x2 Grid Layout
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Panel 1: Live Market Monitor
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("PANEL 1: MARKETS MONITOR", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        marketItems.take(4).forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { onSelectTicker(item.ticker) },
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.ticker, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextPrimary)
                                Text("${item.price}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextPrimary)
                                val isUp = item.change >= 0
                                Text("${if (isUp) "+" else ""}${item.changePct}%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = if (isUp) TerminalGreen else TerminalRed)
                            }
                            Divider(color = TerminalBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }

                // Panel 2: Chart Studio
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("PANEL 2: AAPL US CHART", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        FinancialCanvasChart(
                            prices = chartPrices,
                            chartType = chartType,
                            showSMA = true,
                            showRSI = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Row 2
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Panel 3: Portfolio Summary
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("PANEL 3: PORTFOLIO", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${"%,.2f".format(portfolio.totalValue / 1_000_000.0)}M", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TerminalAmberLight)
                        Text("+$${"%,.0f".format(portfolio.dayChangeDollar)} (+${portfolio.dayChangePct}%)", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Sharpe: ${portfolio.sharpeRatio} | Beta: ${portfolio.beta}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextSecondary)
                    }
                }

                // Panel 4: Live News Feed
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Column(modifier = Modifier.padding(6.dp)) {
                        Text("PANEL 4: LIVE NEWS", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        news.take(2).forEach { n ->
                            Text(n.title, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = TerminalTextPrimary, maxLines = 2)
                            Text(n.timestampStr, fontFamily = FontFamily.Monospace, fontSize = 8.sp, color = TerminalTextMuted)
                            Divider(color = TerminalBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }
    }
}
