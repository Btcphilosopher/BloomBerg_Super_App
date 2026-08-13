package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AiMessage

@Composable
fun AiResearchAssistantScreen(
    aiMessages: List<AiMessage>,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(aiMessages.size) {
        if (aiMessages.isNotEmpty()) {
            listState.animateScrollToItem(aiMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // AI Terminal Header
        Card(
            colors = CardDefaults.cardColors(containerColor = TerminalSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TerminalAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BLOOMBERG INTELLIGENCE AI (AskB)",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TerminalAmber
                    )
                }

                Surface(
                    color = TerminalAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "GEMINI PRO",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = TerminalAmber,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Divider(color = TerminalBorder)

        // Chat Conversation Stream
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(aiMessages) { msg ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = if (msg.isUser) "YOU" else "ASKB AI RESEARCH",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (msg.isUser) TerminalCyan else TerminalAmber
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = msg.timestamp,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = TerminalTextMuted
                        )
                    }

                    Surface(
                        color = if (msg.isUser) TerminalSurfaceVariant else TerminalSurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (msg.isUser) TerminalBorder else TerminalAmber.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (msg.isGeneratedAnalysis) {
                                Surface(
                                    color = TerminalAmber.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = "GENERATED AI ANALYSIS (SYNTHETIC DATA)",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 8.sp,
                                        color = TerminalAmber,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = msg.text,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = TerminalTextPrimary
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TerminalAmber,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Synthesizing market research & order book data...",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TerminalTextSecondary
                        )
                    }
                }
            }
        }

        // Suggested Prompt Chips
        val samplePrompts = listOf(
            "Why is the S&P 500 moving today?",
            "Compare Apple and Microsoft.",
            "Summarise the latest Fed developments.",
            "What companies have the highest exposure to oil prices?",
            "Show me the biggest risks in my portfolio."
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.background(TerminalSurface)
        ) {
            items(samplePrompts) { p ->
                Surface(
                    color = TerminalSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                    modifier = Modifier.clickable { onSendMessage(p) }
                ) {
                    Text(
                        text = p,
                        fontSize = 10.sp,
                        color = TerminalTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Divider(color = TerminalBorder)

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TerminalSurface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Ask Bloomberg AI financial research query...",
                        fontSize = 12.sp,
                        color = TerminalTextMuted
                    )
                },
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
                    .testTag("ai_chat_text_input")
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier.testTag("ai_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Prompt",
                    tint = TerminalAmber
                )
            }
        }
    }
}
