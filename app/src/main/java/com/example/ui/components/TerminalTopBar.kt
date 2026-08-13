package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketItem
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TerminalTopBar(
    marketItems: List<MarketItem>,
    isMobileMode: Boolean,
    alertCount: Int,
    onToggleMobileMode: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenAlerts: () -> Unit,
    onSelectTicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeStr by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'EST'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("America/New_York")
        while (true) {
            currentTimeStr = sdf.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalBlack)
    ) {
        // Top Command Line Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Bloomberg Logo & Terminal Status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = TerminalAmber,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "BLOOMBERG",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "TERMINAL v26.4",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = TerminalTextSecondary
                )

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(TerminalGreen)
                )
            }

            // Live Time & Mode Control
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = currentTimeStr,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = TerminalAmberLight,
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = onOpenSearch,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("top_bar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Ticker",
                        tint = TerminalAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = onOpenAlerts,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("top_bar_alerts_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Alerts",
                            tint = TerminalTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    if (alertCount > 0) {
                        Badge(
                            containerColor = TerminalRed,
                            contentColor = Color.White,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                        ) {
                            Text(text = "$alertCount", fontSize = 9.sp)
                        }
                    }
                }

                Surface(
                    color = if (isMobileMode) TerminalSurfaceVariant else TerminalAmber,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onToggleMobileMode)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = if (isMobileMode) "MOBILE" else "WORKSPACE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = if (isMobileMode) TerminalTextPrimary else Color.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Divider(color = TerminalBorder, thickness = 1.dp)

        // Live Ticker Tape Marquee Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurfaceVariant)
                .padding(vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            marketItems.take(10).forEach { item ->
                Row(
                    modifier = Modifier
                        .clickable { onSelectTicker(item.ticker) }
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.ticker,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = TerminalTextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "%.2f".format(item.price),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TerminalTextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    val isUp = item.change >= 0
                    val badgeBg = if (isUp) TerminalGreen.copy(alpha = 0.2f) else TerminalRed.copy(alpha = 0.2f)
                    val textClr = if (isUp) TerminalGreen else TerminalRed

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text(
                            text = "${if (isUp) "+" else ""}${"%.2f".format(item.changePct)}%",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = textClr,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "|",
                        color = TerminalTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Divider(color = TerminalBorder, thickness = 1.dp)
    }
}
