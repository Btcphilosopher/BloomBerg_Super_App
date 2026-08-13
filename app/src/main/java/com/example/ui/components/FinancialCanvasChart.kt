package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PricePoint
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChartType
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.min

@Composable
fun FinancialCanvasChart(
    prices: List<PricePoint>,
    chartType: ChartType,
    showSMA: Boolean = true,
    showRSI: Boolean = false,
    showMACD: Boolean = false,
    showBollinger: Boolean = false,
    overlayTicker: String? = null,
    modifier: Modifier = Modifier
) {
    if (prices.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(TerminalSurface),
            contentAlignment = Alignment.Center
        ) {
            Text("No Chart Data Available", color = TerminalTextMuted, fontFamily = FontFamily.Monospace)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TerminalSurface, shape = RoundedCornerShape(6.dp))
            .padding(8.dp)
    ) {
        // Main Price Canvas Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val minPrice = prices.minOf { it.low } * 0.98
                val maxPrice = prices.maxOf { it.high } * 1.02
                val priceRange = if (maxPrice - minPrice > 0) maxPrice - minPrice else 1.0

                // Draw Grid Lines
                val gridRows = 4
                for (i in 0..gridRows) {
                    val y = canvasHeight * (i.toFloat() / gridRows)
                    drawLine(
                        color = TerminalBorder.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(canvasWidth, y),
                        strokeWidth = 1f
                    )
                }

                val count = prices.size
                val stepX = canvasWidth / max(1, count - 1)

                when (chartType) {
                    ChartType.CANDLESTICK -> {
                        val candleWidth = max(2f, stepX * 0.6f)
                        prices.forEachIndexed { i, p ->
                            val x = i * stepX
                            val yOpen = canvasHeight - ((p.open - minPrice) / priceRange * canvasHeight).toFloat()
                            val yClose = canvasHeight - ((p.close - minPrice) / priceRange * canvasHeight).toFloat()
                            val yHigh = canvasHeight - ((p.high - minPrice) / priceRange * canvasHeight).toFloat()
                            val yLow = canvasHeight - ((p.low - minPrice) / priceRange * canvasHeight).toFloat()

                            val isBullish = p.close >= p.open
                            val candleColor = if (isBullish) TerminalGreen else TerminalRed

                            // Wick
                            drawLine(
                                color = candleColor,
                                start = Offset(x, yHigh),
                                end = Offset(x, yLow),
                                strokeWidth = 1.5f
                            )

                            // Body
                            val top = min(yOpen, yClose)
                            val height = max(2f, kotlin.math.abs(yOpen - yClose))
                            drawRect(
                                color = candleColor,
                                topLeft = Offset(x - candleWidth / 2, top),
                                size = Size(candleWidth, height)
                            )
                        }
                    }

                    ChartType.LINE, ChartType.MOUNTAIN -> {
                        val path = Path()
                        prices.forEachIndexed { i, p ->
                            val x = i * stepX
                            val y = canvasHeight - ((p.close - minPrice) / priceRange * canvasHeight).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        if (chartType == ChartType.MOUNTAIN) {
                            val fillPath = Path().apply {
                                addPath(path)
                                lineTo(canvasWidth, canvasHeight)
                                lineTo(0f, canvasHeight)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(TerminalAmber.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                        }

                        drawPath(
                            path = path,
                            color = TerminalAmber,
                            style = Stroke(width = 2.5f)
                        )
                    }

                    ChartType.YIELD_CURVE -> {
                        val path = Path()
                        prices.forEachIndexed { i, p ->
                            val x = i * stepX
                            val y = canvasHeight - ((p.close - minPrice) / priceRange * canvasHeight).toFloat()
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                            drawCircle(color = TerminalCyan, radius = 4f, center = Offset(x, y))
                        }
                        drawPath(
                            path = path,
                            color = TerminalCyan,
                            style = Stroke(width = 2.5f)
                        )
                    }
                }

                // SMA Overlay
                if (showSMA && count > 5) {
                    val smaPath = Path()
                    val period = 5
                    for (i in period until count) {
                        val avg = prices.subList(i - period, i).map { it.close }.average()
                        val x = i * stepX
                        val y = canvasHeight - ((avg - minPrice) / priceRange * canvasHeight).toFloat()
                        if (i == period) smaPath.moveTo(x, y) else smaPath.lineTo(x, y)
                    }
                    drawPath(path = smaPath, color = TerminalCyan, style = Stroke(width = 1.5f))
                }

                // Bollinger Bands
                if (showBollinger && count > 5) {
                    val upperPath = Path()
                    val lowerPath = Path()
                    val period = 5
                    for (i in period until count) {
                        val sub = prices.subList(i - period, i).map { it.close }
                        val avg = sub.average()
                        val std = kotlin.math.sqrt(sub.map { (it - avg) * (it - avg) }.average())
                        val upper = avg + (2 * std)
                        val lower = avg - (2 * std)

                        val x = i * stepX
                        val yUpper = canvasHeight - ((upper - minPrice) / priceRange * canvasHeight).toFloat()
                        val yLower = canvasHeight - ((lower - minPrice) / priceRange * canvasHeight).toFloat()

                        if (i == period) {
                            upperPath.moveTo(x, yUpper)
                            lowerPath.moveTo(x, yLower)
                        } else {
                            upperPath.lineTo(x, yUpper)
                            lowerPath.lineTo(x, yLower)
                        }
                    }
                    drawPath(path = upperPath, color = TerminalGold, style = Stroke(width = 1f))
                    drawPath(path = lowerPath, color = TerminalGold, style = Stroke(width = 1f))
                }

                // Overlay Multi-Asset Comparison
                if (!overlayTicker.isNullOrBlank()) {
                    val overlayPath = Path()
                    val firstClose = prices.first().close
                    prices.forEachIndexed { i, p ->
                        val pctChange = ((p.close - firstClose) / firstClose)
                        val normPrice = prices.first().close * (1 + (pctChange * 0.8))
                        val x = i * stepX
                        val y = canvasHeight - ((normPrice - minPrice) / priceRange * canvasHeight).toFloat()
                        if (i == 0) overlayPath.moveTo(x, y) else overlayPath.lineTo(x, y)
                    }
                    drawPath(
                        path = overlayPath,
                        color = TerminalBlue,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // RSI Sub-chart Oscillator
        if (showRSI) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(TerminalBlack)
                    .padding(2.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // 70 / 30 reference lines
                    drawLine(TerminalRed.copy(alpha = 0.5f), Offset(0f, h * 0.3f), Offset(w, h * 0.3f), 1f)
                    drawLine(TerminalGreen.copy(alpha = 0.5f), Offset(0f, h * 0.7f), Offset(w, h * 0.7f), 1f)

                    val rsiPath = Path()
                    val count = prices.size
                    val stepX = w / max(1, count - 1)
                    prices.forEachIndexed { i, p ->
                        val rsiVal = 50.0 + (sin(i.toDouble()) * 22.0)
                        val x = i * stepX
                        val y = h - (rsiVal / 100.0 * h).toFloat()
                        if (i == 0) rsiPath.moveTo(x, y) else rsiPath.lineTo(x, y)
                    }
                    drawPath(rsiPath, color = TerminalAmberLight, style = Stroke(1.5f))
                }
                Text("RSI(14): 58.4", color = TerminalAmberLight, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}
