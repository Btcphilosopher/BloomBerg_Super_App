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
import com.example.data.model.FXPair
import com.example.ui.theme.*

@Composable
fun FxScreen(
    fxPairs: List<FXPair>,
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
                        text = "GLOBAL FOREIGN EXCHANGE (FX) MATRIX",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TerminalAmber
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time G10 spot FX rates, DXY Dollar Index, and 24h volatility.",
                        fontSize = 11.sp,
                        color = TerminalTextSecondary
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerminalSurfaceVariant)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PAIR", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1.2f))
                Text("SPOT RATE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1f))
                Text("CHG %", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(0.8f))
                Text("DAY RANGE", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1.2f))
            }
        }

        items(fxPairs) { fx ->
            val isUp = fx.changePct >= 0
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
                    Text(fx.pair, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalTextPrimary, modifier = Modifier.weight(1.2f))
                    Text("${fx.rate}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalTextPrimary, modifier = Modifier.weight(1f))

                    Surface(
                        color = if (isUp) TerminalGreen.copy(alpha = 0.2f) else TerminalRed.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(3.dp),
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Text(
                            text = "${if (isUp) "+" else ""}${fx.changePct}%",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isUp) TerminalGreen else TerminalRed,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    Text("${fx.low} - ${fx.high}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TerminalTextMuted, modifier = Modifier.weight(1.2f))
                }
            }
        }
    }
}
