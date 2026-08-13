package com.example.data.model

import java.util.UUID

enum class AssetClass(val displayName: String) {
    EQUITIES("Equities"),
    BONDS("Bonds"),
    GOVT_DEBT("Govt Debt"),
    CREDIT("Credit"),
    FX("FX"),
    COMMODITIES("Commodities"),
    CRYPTO("Crypto"),
    RATES("Interest Rates"),
    FUTURES("Futures"),
    OPTIONS("Options"),
    INDICES("Indices")
}

data class PricePoint(
    val timestamp: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long
)

data class MarketItem(
    val ticker: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePct: Double,
    val volume: String,
    val bid: Double,
    val ask: Double,
    val high52: Double,
    val low52: Double,
    val assetClass: AssetClass,
    val history: List<PricePoint> = emptyList(),
    val marketCap: String = "",
    val peRatio: Double = 0.0,
    val rsi: Double = 52.4,
    val macd: Double = 1.25,
    val sma20: Double = 0.0,
    val yieldPct: Double? = null,
    val lastUpdateMs: Long = System.currentTimeMillis(),
    val isUpTick: Boolean? = true
)

data class FinancialStatement(
    val period: String,
    val revenue: String,
    val grossProfit: String,
    val operatingIncome: String,
    val netIncome: String,
    val eps: String,
    val totalAssets: String,
    val totalDebt: String,
    val freeCashFlow: String
)

data class OptionContract(
    val strike: Double,
    val type: String, // "CALL" or "PUT"
    val expiration: String,
    val bid: Double,
    val ask: Double,
    val lastPrice: Double,
    val openInterest: Int,
    val impliedVol: Double
)

data class SecurityDetail(
    val ticker: String,
    val name: String,
    val price: Double,
    val changePct: Double,
    val marketCap: String,
    val peRatio: Double,
    val low52: Double,
    val high52: Double,
    val sector: String,
    val country: String,
    val ceo: String,
    val employees: String,
    val description: String,
    val financials: List<FinancialStatement>,
    val options: List<OptionContract>,
    val ownership: Map<String, Double>, // Entity to % ownership
    val peers: List<String>,
    val analystRatings: Map<String, Int> // "Buy" -> 24, "Hold" -> 8, "Sell" -> 2
)

data class PortfolioHolding(
    val ticker: String,
    val name: String,
    val assetClass: AssetClass,
    val shares: Double,
    val avgCost: Double,
    val currentPrice: Double,
    val pnlPct: Double,
    val weightPct: Double,
    val sector: String,
    val currency: String = "USD"
) {
    val totalValue: Double get() = shares * currentPrice
    val totalCost: Double get() = shares * avgCost
    val pnlDollar: Double get() = totalValue - totalCost
}

data class PortfolioAnalytics(
    val totalValue: Double,
    val dayChangeDollar: Double,
    val dayChangePct: Double,
    val totalReturnDollar: Double,
    val totalReturnPct: Double,
    val assetAllocation: Map<String, Float>,
    val riskLevel: String,
    val volatilityPct: Double,
    val beta: Double,
    val sharpeRatio: Double,
    val maxDrawdownPct: Double,
    val varSimulated95Pct: Double,
    val sectorExposure: Map<String, Float>,
    val geographicExposure: Map<String, Float>,
    val currencyExposure: Map<String, Float>
)

enum class NewsCategory(val displayName: String) {
    BREAKING("Breaking"),
    MARKETS("Markets"),
    COMPANIES("Companies"),
    ECONOMICS("Economics"),
    POLITICS("Politics"),
    TECH("Technology"),
    COMMODITIES("Commodities"),
    GEOPOLITICS("Geopolitics")
}

data class NewsArticle(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val summary: String,
    val category: NewsCategory,
    val timestampStr: String,
    val source: String = "Bloomberg News",
    val tickers: List<String> = emptyList(),
    val isUrgent: Boolean = false,
    val sentiment: String = "NEUTRAL"
)

data class EconomicIndicator(
    val country: String,
    val code: String,
    val name: String,
    val currentValue: String,
    val previousValue: String,
    val forecast: String,
    val unit: String,
    val releaseDate: String,
    val status: String
)

data class FixedIncomeBond(
    val name: String,
    val ticker: String,
    val issuer: String,
    val couponPct: Double,
    val maturity: String,
    val price: Double,
    val yieldToMaturity: Double,
    val durationYears: Double,
    val rating: String,
    val spreadBps: Int
)

data class YieldPoint(
    val maturityLabel: String,
    val years: Double,
    val yieldPct: Double
)

data class FXPair(
    val pair: String,
    val rate: Double,
    val changePct: Double,
    val high: Double,
    val low: Double,
    val dxyWeight: Double = 0.0
)

data class CommodityItem(
    val symbol: String,
    val name: String,
    val price: Double,
    val changePct: Double,
    val unit: String,
    val category: String,
    val high: Double,
    val low: Double
)

data class AlertItem(
    val id: String = UUID.randomUUID().toString(),
    val ticker: String,
    val title: String,
    val condition: String,
    val thresholdValue: String,
    val category: String, // "PRICE", "MACRO", "NEWS"
    val isTriggered: Boolean = false,
    val triggeredTime: String = ""
)

data class EarningsEvent(
    val ticker: String,
    val name: String,
    val time: String, // "16:30", "17:00", "After close"
    val estEps: String,
    val actualEps: String? = null,
    val status: String = "UPCOMING",
    val highlight: String = ""
)

data class CollabMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val role: String,
    val text: String,
    val timestampStr: String,
    val sharedTicker: String? = null,
    val sharedChartType: String? = null
)

data class SavedWorkspace(
    val name: String,
    val panels: List<String>
)
