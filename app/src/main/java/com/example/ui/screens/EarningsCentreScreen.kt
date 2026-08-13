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
import com.example.data.model.EarningsEvent
import com.example.ui.theme.*

@Composable
fun EarningsCentreScreen(
    earningsEvents: List<EarningsEvent>,
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
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CORPORATE EARNINGS CALENDAR",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TerminalAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quarterly consensus EPS estimates, revenue surprises, and release times.",
                        fontSize = 11.sp,
                        color = TerminalTextSecondary
                    )
                }
            }
        }

        items(earningsEvents) { event ->
            val isBeat = event.status == "BEAT"
            Card(
                colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(event.ticker, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalAmber)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(event.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TerminalTextPrimary)
                        }

                        Surface(
                            color = if (isBeat) TerminalGreen.copy(alpha = 0.2f) else TerminalSurfaceVariant,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = event.status,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isBeat) TerminalGreen else TerminalCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Release: ${event.time}", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalTextMuted)
                        Text("Est: ${event.estEps} | Act: ${event.actualEps ?: "Pending"}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TerminalAmberLight)
                    }

                    if (!event.highlight.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(event.highlight, fontSize = 11.sp, color = TerminalTextSecondary)
                    }
                }
            }
        }
    }
}
