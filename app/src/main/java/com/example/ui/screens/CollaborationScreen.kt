package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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
import com.example.data.model.CollabMessage
import com.example.ui.theme.*

@Composable
fun CollaborationScreen(
    chatMessages: List<CollabMessage>,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "BLOOMBERG INSTANT MESSAGING (IB)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TerminalAmber
                )
                Text(
                    text = "Secure institutional collaboration workspace desk.",
                    fontSize = 11.sp,
                    color = TerminalTextSecondary
                )
            }
        }

        Divider(color = TerminalBorder)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(msg.sender, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TerminalAmber)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(${msg.role})", fontSize = 10.sp, color = TerminalTextMuted)
                            }
                            Text(msg.timestampStr, fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalAmberLight)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(msg.text, fontSize = 12.sp, color = TerminalTextPrimary)

                        if (!msg.sharedTicker.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                color = TerminalSurfaceVariant,
                                shape = RoundedCornerShape(3.dp)
                            ) {
                                Text("ATTACHED TICKER: ${msg.sharedTicker}", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = TerminalCyan, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }

        Divider(color = TerminalBorder)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Send IB collaboration message...", fontSize = 12.sp, color = TerminalTextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerminalAmber,
                    unfocusedBorderColor = TerminalBorder,
                    focusedContainerColor = TerminalBlack,
                    unfocusedContainerColor = TerminalBlack,
                    focusedTextColor = TerminalTextPrimary,
                    unfocusedTextColor = TerminalTextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("collab_chat_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (chatInput.isNotBlank()) {
                        onSendMessage(chatInput)
                        chatInput = ""
                    }
                },
                modifier = Modifier.testTag("collab_chat_send_button")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send", tint = TerminalAmber)
            }
        }
    }
}
