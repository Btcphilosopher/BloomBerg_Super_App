package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketItem
import com.example.data.model.NewsArticle
import com.example.data.model.PortfolioAnalytics
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab

@Composable
fun HomeMarketCentreScreen(
    marketItems: List<MarketItem>,
    topNews: List<NewsArticle>,
    portfolio: PortfolioAnalytics,
    onSelectTicker: (String) -> Unit,
    onNavigateTab: (MainTab) -> Unit,
    onAskAiPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Section: BLOOMBERG MARKETS COMMAND CENTRE
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
                            text = "BLOOMBERG MARKETS",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TerminalAmber
                        )

                        Text(
                            text = "COMMAND CENTRE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TerminalTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Markets Ticker Grid
                    val spx = marketItems.find { it.ticker == "SPX" }
                    val ndx = marketItems.find { it.ticker == "NDX" }
                    val ftse = marketItems.find { it.ticker == "UKX" }
                    val dax = marketItems.find { it.ticker == "DAX" }
                    val btc = marketItems.find { it.ticker == "BTCUSD" }

                    val indicesList = listOfNotNull(
                        spx ?: MarketItem("SPX", "S&P 500", 6842.12, 28.50, 0.42, "3.4B", 0.0, 0.0, 0.0, 0.0, com.example.data.model.AssetClass.INDICES),
                        ndx ?: MarketItem("NDX", "NASDAQ", 22481.30, 158.40, 0.71, "4.1B", 0.0, 0.0, 0.0, 0.0, com.example.data.model.AssetClass.INDICES),
                        ftse ?: MarketItem("UKX", "FTSE 100", 9214.82, -16.60, -0.18, "842M", 0.0, 0.0, 0.0, 0.0, com.example.data.model.AssetClass.INDICES),
                        dax ?: MarketItem("DAX", "DAX 40", 24182.40, 74.80, 0.31, "1.1B", 0.0, 0.0, 0.0, 0.0, com.example.data.model.AssetClass.INDICES),
                        btc ?: MarketItem("BTCUSD", "BTC USD", 118420.0, 2480.0, 2.14, "$42B", 0.0, 0.0, 0.0, 0.0, com.example.data.model.AssetClass.CRYPTO)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(indicesList) { item ->
                            val isUp = item.change >= 0
                            Surface(
                                color = TerminalSurfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                                modifier = Modifier
                                    .width(130.dp)
                                    .clickable { onSelectTicker(item.ticker) }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = item.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TerminalTextSecondary,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (item.ticker == "BTCUSD") "$${"%,.0f".format(item.price)}" else "%,.2f".format(item.price),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TerminalTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${if (isUp) "+" else ""}${"%.2f".format(item.changePct)}%",
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isUp) TerminalGreen else TerminalRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section: YOUR PORTFOLIO SUMMARY
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(MainTab.PORTFOLIO) }
                    .testTag("home_portfolio_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR PORTFOLIO",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TerminalTextPrimary
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to Portfolio",
                            tint = TerminalAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "$${"%,.2f".format(portfolio.totalValue / 1_000_000.0)}M",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                color = TerminalAmberLight
                            )
                            Text(
                                text = "+$${"%,.2f".format(portfolio.dayChangeDollar)} today",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = TerminalGreen
                            )
                        }

                        Surface(
                            color = TerminalGreen.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "+${portfolio.dayChangePct}%",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TerminalGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Asset Class Allocation Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("EQUITIES 48%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalCyan)
                        Text("BONDS 27%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalAmber)
                        Text("CASH 8%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextSecondary)
                        Text("COMMODITIES 7%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalGold)
                        Text("OTHER 10%", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted)
                    }
                }
            }
        }

        // Section: AI RESEARCH PROMPT SHORTCUTS
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TerminalAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ASK BLOOMBERG INTELLIGENCE AI",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TerminalAmber
                    )
                }

                val prompts = listOf(
                    "Why is the S&P 500 moving today?",
                    "Compare Apple and Microsoft.",
                    "Summarise the latest Fed developments.",
                    "Show me the biggest risks in my portfolio."
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(prompts) { prompt ->
                        Surface(
                            color = TerminalSurfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                            modifier = Modifier.clickable {
                                onAskAiPrompt(prompt)
                                onNavigateTab(MainTab.AI)
                            }
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = TerminalTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: TOP NEWS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TOP BREAKING NEWS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TerminalTextPrimary
                )

                Text(
                    text = "SEE ALL >",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TerminalAmber,
                    modifier = Modifier.clickable { onNavigateTab(MainTab.NEWS) }
                )
            }
        }

        items(topNews.take(3)) { article ->
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(MainTab.NEWS) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (article.isUrgent) TerminalRed.copy(alpha = 0.2f) else TerminalSurfaceVariant,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = article.category.displayName.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (article.isUrgent) TerminalRed else TerminalAmber,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = article.timestampStr,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TerminalTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = article.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TerminalTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = article.summary,
                        fontSize = 11.sp,
                        color = TerminalTextSecondary,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
