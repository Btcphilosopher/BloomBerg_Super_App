package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.AlertEntity
import com.example.data.local.WatchlistEntity
import com.example.ui.theme.*

@Composable
fun WatchlistsAlertsScreen(
    watchlist: List<WatchlistEntity>,
    alerts: List<AlertEntity>,
    onSelectTicker: (String) -> Unit,
    onDeleteWatchlist: (String) -> Unit,
    onAddAlert: (String, String, String, String, String) -> Unit,
    onDeleteAlert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddAlertModal by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Watchlists Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM WATCHLISTS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TerminalAmber
                )
                Text("${watchlist.size} Securities Saved", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TerminalTextMuted)
            }
        }

        if (watchlist.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No saved watchlist securities. Tap star icon in Markets to add.", color = TerminalTextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(watchlist) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectTicker(item.ticker) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.ticker, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalTextPrimary)
                            Text(item.name, fontSize = 11.sp, color = TerminalTextSecondary)
                        }

                        IconButton(
                            onClick = { onDeleteWatchlist(item.ticker) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TerminalRed)
                        }
                    }
                }
            }
        }

        // Configurable Alerts Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL-TIME MARKET ALERTS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TerminalAmber
                )

                Button(
                    onClick = { showAddAlertModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerminalAmber, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("add_alert_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NEW ALERT", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (alerts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No active threshold alerts. Tap NEW ALERT to configure.", color = TerminalTextMuted, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(alerts) { alert ->
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
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = TerminalSurfaceVariant,
                                    shape = RoundedCornerShape(3.dp)
                                ) {
                                    Text(alert.ticker, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = TerminalAmber, modifier = Modifier.padding(4.dp))
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TerminalTextPrimary)
                            }
                            Text("${alert.condition} ${alert.thresholdValue}", fontSize = 11.sp, color = TerminalTextSecondary)
                        }

                        IconButton(
                            onClick = { onDeleteAlert(alert.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Alert", tint = TerminalRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddAlertModal) {
        var alertTicker by remember { mutableStateOf("AAPL US") }
        var alertCondition by remember { mutableStateOf("PRICE >") }
        var alertValue by remember { mutableStateOf("250.00") }

        Dialog(onDismissRequest = { showAddAlertModal = false }) {
            Surface(
                color = TerminalSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CONFIGURE MARKET ALERT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TerminalAmber)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = alertTicker,
                        onValueChange = { alertTicker = it },
                        label = { Text("Ticker Symbol", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = alertCondition,
                        onValueChange = { alertCondition = it },
                        label = { Text("Condition e.g. PRICE >, VOLATILITY >", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = alertValue,
                        onValueChange = { alertValue = it },
                        label = { Text("Threshold Value", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddAlertModal = false }) {
                            Text("CANCEL", color = TerminalTextMuted)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onAddAlert(alertTicker, "Price Threshold Alert", alertCondition, alertValue, "PRICE")
                                showAddAlertModal = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TerminalAmber, contentColor = Color.Black)
                        ) {
                            Text("SAVE ALERT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
