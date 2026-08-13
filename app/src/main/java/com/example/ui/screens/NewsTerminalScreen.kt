package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.NewsArticle
import com.example.data.model.NewsCategory
import com.example.ui.theme.*

@Composable
fun NewsTerminalScreen(
    newsArticles: List<NewsArticle>,
    selectedCategory: NewsCategory?,
    onSelectCategory: (NewsCategory?) -> Unit,
    onSelectTicker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedArticleForModal by remember { mutableStateOf<NewsArticle?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // News Categories Filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.background(TerminalSurface)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text("ALL FEED", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerminalAmber,
                        selectedLabelColor = Color.Black
                    )
                )
            }

            items(NewsCategory.values()) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat.displayName.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerminalAmber,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Divider(color = TerminalBorder)

        // News Stream List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val filteredList = newsArticles.filter { selectedCategory == null || it.category == selectedCategory }

            items(filteredList) { article ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = TerminalSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (article.isUrgent) TerminalRed else TerminalBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedArticleForModal = article }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (article.isUrgent) TerminalRed else TerminalSurfaceVariant,
                                    shape = RoundedCornerShape(3.dp)
                                ) {
                                    Text(
                                        text = article.category.displayName.uppercase(),
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = if (article.isUrgent) Color.White else TerminalAmber,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = article.source,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TerminalTextMuted
                                )
                            }

                            Text(
                                text = article.timestampStr,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TerminalAmberLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = article.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TerminalTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = article.summary,
                            fontSize = 12.sp,
                            color = TerminalTextSecondary,
                            maxLines = 3
                        )

                        if (article.tickers.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                article.tickers.forEach { ticker ->
                                    Surface(
                                        color = TerminalSurfaceVariant,
                                        shape = RoundedCornerShape(3.dp),
                                        modifier = Modifier.clickable { onSelectTicker(ticker) }
                                    ) {
                                        Text(
                                            text = ticker,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = TerminalCyan,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Article Reader Dialog
    selectedArticleForModal?.let { article ->
        Dialog(onDismissRequest = { selectedArticleForModal = null }) {
            Surface(
                color = TerminalSurface,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, TerminalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BLOOMBERG NEWS TERMINAL",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TerminalAmber
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = article.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TerminalTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${article.source} • ${article.timestampStr}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TerminalTextSecondary
                    )

                    Divider(color = TerminalBorder, modifier = Modifier.padding(vertical = 10.dp))

                    Text(
                        text = "${article.summary}\n\n[FULL DISPATCH]: Institutional desks report heightened order flow execution across related asset classes following this news bulletin. Bloomberg Intelligence analysts recommend monitoring key resistance thresholds in corresponding futures contracts.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TerminalTextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { selectedArticleForModal = null },
                        colors = ButtonDefaults.buttonColors(containerColor = TerminalAmber, contentColor = Color.Black),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CLOSE DISPATCH", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
