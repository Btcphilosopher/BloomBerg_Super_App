package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
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
import com.example.data.model.AssetClass
import com.example.data.model.MarketItem
import com.example.ui.theme.*

@Composable
fun MarketsCenterScreen(
    marketItems: List<MarketItem>,
    selectedAssetClass: AssetClass?,
    watchlistTickers: List<String>,
    onSelectAssetClass: (AssetClass?) -> Unit,
    onSelectTicker: (String) -> Unit,
    onToggleWatchlist: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // Asset Class Filter Bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.background(TerminalSurface)
        ) {
            item {
                FilterChip(
                    selected = selectedAssetClass == null,
                    onClick = { onSelectAssetClass(null) },
                    label = { Text("ALL MARKETS", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerminalAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = TerminalSurfaceVariant,
                        labelColor = TerminalTextPrimary
                    )
                )
            }

            items(AssetClass.values()) { assetClass ->
                FilterChip(
                    selected = selectedAssetClass == assetClass,
                    onClick = { onSelectAssetClass(assetClass) },
                    label = { Text(assetClass.displayName.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerminalAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = TerminalSurfaceVariant,
                        labelColor = TerminalTextPrimary
                    )
                )
            }
        }

        Divider(color = TerminalBorder)

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TICKER / NAME", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1.3f))
            Text("BID / ASK", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1f))
            Text("PRICE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(0.9f))
            Text("CHG %", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(0.8f))
            Text("FAV", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.width(32.dp))
        }

        Divider(color = TerminalBorder)

        // Market Data Rows
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(marketItems) { item ->
                val isSaved = watchlistTickers.contains(item.ticker)
                val isUp = item.change >= 0

                val flashColor by animateColorAsState(
                    targetValue = when (item.isUpTick) {
                        true -> TerminalGreen.copy(alpha = 0.15f)
                        false -> TerminalRed.copy(alpha = 0.15f)
                        null -> TerminalSurface
                    },
                    label = "flashAnimation"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(flashColor)
                        .clickable { onSelectTicker(item.ticker) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ticker & Name
                    Column(modifier = Modifier.weight(1.3f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.ticker,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TerminalTextPrimary
                            )
                        }
                        Text(
                            text = item.name,
                            fontSize = 11.sp,
                            color = TerminalTextSecondary,
                            maxLines = 1
                        )
                    }

                    // Bid / Ask
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${"%.2f".format(item.bid)} / ${"%.2f".format(item.ask)}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TerminalTextSecondary
                        )
                        Text(
                            text = "Vol ${item.volume}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TerminalTextMuted
                        )
                    }

                    // Price
                    Text(
                        text = if (item.assetClass == AssetClass.CRYPTO) "$${"%,.0f".format(item.price)}" else "%.2f".format(item.price),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TerminalTextPrimary,
                        modifier = Modifier.weight(0.9f)
                    )

                    // Change % Badge
                    Surface(
                        color = if (isUp) TerminalGreen.copy(alpha = 0.2f) else TerminalRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text(
                            text = "${if (isUp) "+" else ""}${"%.2f".format(item.changePct)}%",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isUp) TerminalGreen else TerminalRed,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    // Watchlist Star Toggle
                    IconButton(
                        onClick = { onToggleWatchlist(item.ticker, item.name, item.assetClass.displayName) },
                        modifier = Modifier
                            .width(32.dp)
                            .testTag("star_${item.ticker}")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Watchlist Toggle",
                            tint = if (isSaved) TerminalAmber else TerminalTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Divider(color = TerminalBorder.copy(alpha = 0.4f))
            }
        }
    }
}
