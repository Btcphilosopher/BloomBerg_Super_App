package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PricePoint
import com.example.data.model.SecurityDetail
import com.example.ui.components.FinancialCanvasChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChartType

enum class SecurityTab(val label: String) {
    CHART("CHART"),
    NEWS("NEWS"),
    RESEARCH("RESEARCH"),
    FINANCIALS("FINANCIALS"),
    OWNERSHIP("OWNERSHIP"),
    OPTIONS("OPTIONS"),
    PEERS("PEERS")
}

@Composable
fun SecurityTerminalScreen(
    security: SecurityDetail,
    chartPrices: List<PricePoint>,
    chartType: ChartType,
    showSMA: Boolean,
    showRSI: Boolean,
    showMACD: Boolean,
    showBollinger: Boolean,
    overlayTicker: String?,
    onChartTypeChange: (ChartType) -> Unit,
    onToggleSMA: () -> Unit,
    onToggleRSI: () -> Unit,
    onToggleMACD: () -> Unit,
    onToggleBollinger: () -> Unit,
    onOverlayChange: (String) -> Unit,
    onSelectPeer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf(SecurityTab.CHART) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // High-Density Header
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = TerminalAmber,
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text(
                                    text = security.ticker,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = security.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TerminalTextPrimary
                            )
                        }
                        Text(
                            text = "${security.sector} • ${security.country}",
                            fontSize = 11.sp,
                            color = TerminalTextSecondary
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${"%.2f".format(security.price)}",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TerminalTextPrimary
                        )
                        val isUp = security.changePct >= 0
                        Surface(
                            color = if (isUp) TerminalGreen.copy(alpha = 0.2f) else TerminalRed.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = "${if (isUp) "+" else ""}${"%.2f".format(security.changePct)}%",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isUp) TerminalGreen else TerminalRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Bar Grid
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalSurfaceVariant, shape = RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("MARKET CAP", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                        Text(security.marketCap, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalTextPrimary)
                    }
                    Column {
                        Text("P/E RATIO", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                        Text("${security.peRatio}x", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalTextPrimary)
                    }
                    Column {
                        Text("52-WK RANGE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalTextMuted)
                        Text("$${security.low52.toInt()} — $${security.high52.toInt()}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalTextPrimary)
                    }
                }
            }
        }

        // Section Tabs Header
        ScrollableTabRow(
            selectedTabIndex = activeSubTab.ordinal,
            containerColor = TerminalSurface,
            contentColor = TerminalAmber,
            edgePadding = 8.dp,
            divider = { Divider(color = TerminalBorder) }
        ) {
            SecurityTab.values().forEach { tab ->
                Tab(
                    selected = activeSubTab == tab,
                    onClick = { activeSubTab = tab },
                    text = {
                        Text(
                            text = tab.label,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (activeSubTab == tab) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.sp
                        )
                    }
                )
            }
        }

        // Sub Tab Content Panel
        when (activeSubTab) {
            SecurityTab.CHART -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    // Chart Control Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = chartType == ChartType.CANDLESTICK,
                                onClick = { onChartTypeChange(ChartType.CANDLESTICK) },
                                label = { Text("CANDLE", fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                            )
                            FilterChip(
                                selected = chartType == ChartType.MOUNTAIN,
                                onClick = { onChartTypeChange(ChartType.MOUNTAIN) },
                                label = { Text("MOUNTAIN", fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = showSMA,
                                onClick = onToggleSMA,
                                label = { Text("SMA", fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                            )
                            FilterChip(
                                selected = showRSI,
                                onClick = onToggleRSI,
                                label = { Text("RSI", fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                            )
                            FilterChip(
                                selected = showBollinger,
                                onClick = onToggleBollinger,
                                label = { Text("BOLL", fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                            )
                        }
                    }

                    // Interactive Chart
                    FinancialCanvasChart(
                        prices = chartPrices,
                        chartType = chartType,
                        showSMA = showSMA,
                        showRSI = showRSI,
                        showMACD = showMACD,
                        showBollinger = showBollinger,
                        overlayTicker = overlayTicker,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }

            SecurityTab.RESEARCH -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("ANALYST CONSENSUS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmber)
                                Spacer(modifier = Modifier.height(8.dp))
                                security.analystRatings.forEach { (rating, count) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(rating, fontSize = 12.sp, color = TerminalTextPrimary)
                                        Text("$count Analysts", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalAmberLight)
                                    }
                                    Divider(color = TerminalBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("BUSINESS PROFILE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmber)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("CEO: ${security.ceo} | Employees: ${security.employees}", fontSize = 11.sp, color = TerminalTextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(security.description, fontSize = 12.sp, color = TerminalTextPrimary)
                            }
                        }
                    }
                }
            }

            SecurityTab.FINANCIALS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    items(security.financials) { fin ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("PERIOD: ${fin.period}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmber)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Revenue", fontSize = 12.sp, color = TerminalTextSecondary)
                                    Text(fin.revenue, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalTextPrimary)
                                }
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Operating Income", fontSize = 12.sp, color = TerminalTextSecondary)
                                    Text(fin.operatingIncome, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalTextPrimary)
                                }
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("Net Income", fontSize = 12.sp, color = TerminalTextSecondary)
                                    Text(fin.netIncome, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalGreen)
                                }
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                                    Text("EPS", fontSize = 12.sp, color = TerminalTextSecondary)
                                    Text(fin.eps, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TerminalAmberLight)
                                }
                            }
                        }
                    }
                }
            }

            SecurityTab.OWNERSHIP -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    item {
                        Text("INSTITUTIONAL HOLDINGS BREAKDOWN", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(security.ownership.toList()) { (holder, pct) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TerminalSurface)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(holder, fontSize = 12.sp, color = TerminalTextPrimary)
                            Text("$pct%", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalCyan)
                        }
                        Divider(color = TerminalBorder)
                    }
                }
            }

            SecurityTab.OPTIONS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TerminalSurfaceVariant)
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("STRIKE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("TYPE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("BID / ASK", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                            Text("IV %", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                        }
                    }

                    items(security.options) { opt ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("$${opt.strike}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalTextPrimary)
                            Text(opt.type, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = if (opt.type == "CALL") TerminalGreen else TerminalRed)
                            Text("${"%.2f".format(opt.bid)} / ${"%.2f".format(opt.ask)}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalTextSecondary)
                            Text("${opt.impliedVol}%", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalAmber)
                        }
                        Divider(color = TerminalBorder.copy(alpha = 0.4f))
                    }
                }
            }

            SecurityTab.PEERS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    item {
                        Text("COMPETITOR & PEER GROUP", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalAmber)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(security.peers) { peer ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectPeer(peer) }
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(peer, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalTextPrimary)
                                Text("COMPARE / VIEW >", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalAmber)
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }
}
