package com.example.data.repository

import com.example.data.local.AlertEntity
import com.example.data.local.TerminalDao
import com.example.data.local.WatchlistEntity
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.random.Random

class MarketRepository(
    private val dao: TerminalDao,
    private val externalScope: CoroutineScope
) {
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    // Live streaming market items
    private val _marketItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val marketItems: StateFlow<List<MarketItem>> = _marketItems.asStateFlow()

    // News Articles
    private val _newsFeed = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsFeed: StateFlow<List<NewsArticle>> = _newsFeed.asStateFlow()

    // Collaboration Chat Stream
    private val _chatMessages = MutableStateFlow<List<CollabMessage>>(emptyList())
    val chatMessages: StateFlow<List<CollabMessage>> = _chatMessages.asStateFlow()

    // Room Watchlist & Alerts
    val watchlistTickers: Flow<List<WatchlistEntity>> = dao.getWatchlist()
    val userAlerts: Flow<List<AlertEntity>> = dao.getAlerts()

    init {
        _marketItems.value = generateInitialMarketItems()
        _newsFeed.value = generateInitialNews()
        _chatMessages.value = generateInitialChat()

        // Start live ticker streaming simulation
        externalScope.launch(Dispatchers.Default) {
            while (true) {
                delay(2000)
                updateMarketTickers()
            }
        }
    }

    private fun generateInitialMarketItems(): List<MarketItem> {
        val now = System.currentTimeMillis()
        val items = mutableListOf<MarketItem>()

        fun makeHistory(base: Double, volatility: Double): List<PricePoint> {
            val list = mutableListOf<PricePoint>()
            var p = base * 0.95
            for (i in 30 downTo 1) {
                val change = (Random.nextDouble() - 0.48) * volatility
                val open = p
                val close = p + change
                val high = maxOf(open, close) + Random.nextDouble() * (volatility * 0.5)
                val low = minOf(open, close) - Random.nextDouble() * (volatility * 0.5)
                list.add(PricePoint(now - (i * 86400000L), open, high, low, close, Random.nextLong(10000, 500000)))
                p = close
            }
            return list
        }

        // INDICES
        items.add(MarketItem("SPX", "S&P 500 Index", 6842.12, 28.50, 0.42, "3.42B", 6841.80, 6842.50, 6850.0, 6790.0, AssetClass.INDICES, makeHistory(6800.0, 25.0)))
        items.add(MarketItem("NDX", "NASDAQ 100", 22481.30, 158.40, 0.71, "4.18B", 22480.00, 22482.50, 22510.0, 22300.0, AssetClass.INDICES, makeHistory(22300.0, 120.0)))
        items.add(MarketItem("UKX", "FTSE 100", 9214.82, -16.60, -0.18, "842M", 9214.10, 9215.50, 9240.0, 9190.0, AssetClass.INDICES, makeHistory(9200.0, 15.0)))
        items.add(MarketItem("DAX", "DAX 40 Germany", 24182.40, 74.80, 0.31, "1.12B", 24180.00, 24185.00, 24220.0, 24100.0, AssetClass.INDICES, makeHistory(24000.0, 80.0)))

        // EQUITIES
        items.add(MarketItem("AAPL US", "Apple Inc.", 242.80, 2.95, 1.23, "48.2M", 242.75, 242.85, 248.0, 165.0, AssetClass.EQUITIES, makeHistory(240.0, 3.0), marketCap = "$3.72T", peRatio = 32.4))
        items.add(MarketItem("MSFT US", "Microsoft Corp.", 448.50, 4.10, 0.92, "22.1M", 448.40, 448.60, 468.0, 385.0, AssetClass.EQUITIES, makeHistory(440.0, 4.0), marketCap = "$3.33T", peRatio = 34.2))
        items.add(MarketItem("NVDA US", "NVIDIA Corp.", 138.20, 3.80, 2.83, "82.4M", 138.15, 138.25, 140.0, 75.0, AssetClass.EQUITIES, makeHistory(130.0, 2.5), marketCap = "$3.39T", peRatio = 48.5))
        items.add(MarketItem("TSLA US", "Tesla Inc.", 218.40, -4.20, -1.89, "34.8M", 218.30, 218.50, 271.0, 138.0, AssetClass.EQUITIES, makeHistory(220.0, 5.0), marketCap = "$694B", peRatio = 62.1))
        items.add(MarketItem("GOOGL US", "Alphabet Inc.", 182.30, 1.40, 0.77, "18.5M", 182.20, 182.40, 191.0, 131.0, AssetClass.EQUITIES, makeHistory(180.0, 2.0), marketCap = "$2.26T", peRatio = 24.8))

        // CRYPTO
        items.add(MarketItem("BTCUSD", "Bitcoin USD", 118420.00, 2480.00, 2.14, "$42.8B", 118410.0, 118430.0, 122000.0, 58000.0, AssetClass.CRYPTO, makeHistory(115000.0, 1500.0)))
        items.add(MarketItem("ETHUSD", "Ethereum USD", 3840.50, 120.20, 3.23, "$18.2B", 3840.0, 3841.0, 4100.0, 2200.0, AssetClass.CRYPTO, makeHistory(3700.0, 80.0)))

        // FX
        items.add(MarketItem("EURUSD", "EUR/USD Currency", 1.1842, 0.0032, 0.27, "128B", 1.1841, 1.1843, 1.1920, 1.0650, AssetClass.FX, makeHistory(1.18, 0.005)))
        items.add(MarketItem("GBPUSD", "GBP/USD Currency", 1.3721, -0.0018, -0.13, "94B", 1.3720, 1.3722, 1.3850, 1.2400, AssetClass.FX, makeHistory(1.37, 0.006)))
        items.add(MarketItem("USDJPY", "USD/JPY Currency", 148.42, 0.45, 0.30, "112B", 148.40, 148.44, 161.00, 140.00, AssetClass.FX, makeHistory(148.0, 0.8)))

        // COMMODITIES
        items.add(MarketItem("BRENT", "Brent Crude Oil", 78.40, 1.10, 1.42, "482K", 78.38, 78.42, 92.0, 71.0, AssetClass.COMMODITIES, makeHistory(77.0, 1.2)))
        items.add(MarketItem("XAUUSD", "Gold Spot USD/oz", 2740.50, 18.20, 0.67, "1.2M", 2740.0, 2741.0, 2790.0, 2150.0, AssetClass.COMMODITIES, makeHistory(2720.0, 15.0)))
        items.add(MarketItem("XAGUSD", "Silver Spot USD/oz", 32.40, 0.45, 1.41, "340K", 32.38, 32.42, 35.0, 22.0, AssetClass.COMMODITIES, makeHistory(31.5, 0.4)))

        // BONDS & GOVT DEBT
        items.add(MarketItem("US10Y", "US Treasury 10-Yr Yield", 4.214, -0.042, -0.99, "8.4B", 4.212, 4.216, 5.02, 3.78, AssetClass.GOVT_DEBT, makeHistory(4.25, 0.03), yieldPct = 4.214))
        items.add(MarketItem("US02Y", "US Treasury 2-Yr Yield", 4.128, -0.035, -0.84, "12.1B", 4.126, 4.130, 5.20, 3.85, AssetClass.GOVT_DEBT, makeHistory(4.15, 0.03), yieldPct = 4.128))

        return items
    }

    private fun updateMarketTickers() {
        val updated = _marketItems.value.map { item ->
            val factor = (Random.nextDouble() - 0.49) * 0.004
            val delta = item.price * factor
            val newPrice = (item.price + delta).let { if (it < 0.01) 0.01 else it }
            val newChange = item.change + delta
            val newChangePct = (newChange / (item.price - item.change)) * 100
            val isUp = delta >= 0
            item.copy(
                price = (newPrice * 100).toLong() / 100.0,
                change = (newChange * 100).toLong() / 100.0,
                changePct = (newChangePct * 100).toLong() / 100.0,
                lastUpdateMs = System.currentTimeMillis(),
                isUpTick = isUp
            )
        }
        _marketItems.value = updated
    }

    private fun generateInitialNews(): List<NewsArticle> {
        return listOf(
            NewsArticle(
                title = "FED SIGNALS POTENTIAL RATE CHANGE AT UPCOMING FOMC MEETING",
                summary = "Chair Powell highlights disinflationary progress in PCE data while emphasizing labor market stability and balanced risks.",
                category = NewsCategory.BREAKING,
                timestampStr = "09:42 EST",
                tickers = listOf("SPX", "US10Y", "EURUSD"),
                isUrgent = true,
                sentiment = "BULLISH"
            ),
            NewsArticle(
                title = "EUROPEAN STOCKS RISE AS INVESTORS ASSESS GERMAN INFLATION DATA",
                summary = "DAX 40 ticks higher by 0.31% following lower-than-expected regional CPI prints across North Rhine-Westphalia.",
                category = NewsCategory.MARKETS,
                timestampStr = "09:39 EST",
                tickers = listOf("DAX", "UKX"),
                sentiment = "BULLISH"
            ),
            NewsArticle(
                title = "CRUDE OIL SURGES PAST \$78/BBL ON MIDDLE EAST SUPPLY RISK RE-PRICING",
                summary = "Brent crude futures jump 1.4% as tanker transit metrics in key choke points show heightened risk premiums.",
                category = NewsCategory.COMMODITIES,
                timestampStr = "09:25 EST",
                tickers = listOf("BRENT"),
                sentiment = "BEARISH"
            ),
            NewsArticle(
                title = "APPLE ANNOUNCES NEW M5 AI SILICON & ENTERPRISE SERVICES SUBSCRIPTION",
                summary = "Cupertino tech giant expands hardware ecosystem with integrated neural engines targeting enterprise generative AI agents.",
                category = NewsCategory.COMPANIES,
                timestampStr = "08:50 EST",
                tickers = listOf("AAPL US"),
                sentiment = "BULLISH"
            ),
            NewsArticle(
                title = "NVIDIA NETWORKING REVENUE SOARS 42% YoY IN AI DATACENTER BOOM",
                summary = "Spectrum-X ethernet switches and Infiniband architecture drive record gross margin guidance for next quarter.",
                category = NewsCategory.TECH,
                timestampStr = "08:15 EST",
                tickers = listOf("NVDA US", "MSFT US"),
                sentiment = "BULLISH"
            )
        )
    }

    private fun generateInitialChat(): List<CollabMessage> {
        return listOf(
            CollabMessage(
                sender = "Marcus Vance",
                role = "Senior Macro Strategist",
                text = "US 10Y yield breaching 4.21% support. Watching SPX 6,850 level closely.",
                timestampStr = "09:38 EST",
                sharedTicker = "US10Y"
            ),
            CollabMessage(
                sender = "Elena Rostova",
                role = "Head of Derivatives Desk",
                text = "AAPL call options open interest spiking at $250 strike for next week expiration. Vol smile skewing bullish.",
                timestampStr = "09:40 EST",
                sharedTicker = "AAPL US",
                sharedChartType = "OPTIONS"
            ),
            CollabMessage(
                sender = "Bloomberg Quant Desk",
                role = "Automated System",
                text = "ALERT: Sector rotation signal triggered from Energy into Semiconductors & Software.",
                timestampStr = "09:41 EST"
            )
        )
    }

    fun getSecurityDetail(ticker: String): SecurityDetail {
        val baseItem = _marketItems.value.find { it.ticker == ticker }
        val name = baseItem?.name ?: ticker
        val price = baseItem?.price ?: 242.80
        val changePct = baseItem?.changePct ?: 1.23

        return SecurityDetail(
            ticker = ticker,
            name = name,
            price = price,
            changePct = changePct,
            marketCap = baseItem?.marketCap.takeIf { !it.isNullOrBlank() } ?: "$3.72T",
            peRatio = if (baseItem?.peRatio ?: 0.0 > 0) baseItem!!.peRatio else 32.4,
            low52 = baseItem?.low52 ?: 165.0,
            high52 = baseItem?.high52 ?: 248.0,
            sector = "Technology / Consumer Electronics",
            country = "United States",
            ceo = "Tim Cook",
            employees = "164,000",
            description = "$name is a global technology enterprise engaged in designing, manufacturing, and marketing consumer electronics, cloud services, software solutions, and artificial intelligence hardware ecosystems.",
            financials = listOf(
                FinancialStatement("2025 FY", "$391.0B", "$180.7B", "$123.2B", "$93.7B", "$6.11", "$352.6B", "$104.5B", "$108.8B"),
                FinancialStatement("2024 FY", "$383.3B", "$170.7B", "$114.3B", "$97.0B", "$6.16", "$364.9B", "$106.6B", "$108.8B"),
                FinancialStatement("2023 FY", "$383.3B", "$169.1B", "$114.3B", "$97.0B", "$6.13", "$352.6B", "$111.1B", "$99.6B")
            ),
            options = listOf(
                OptionContract(240.0, "CALL", "15 AUG 2026", 5.20, 5.35, 5.28, 14200, 24.5),
                OptionContract(245.0, "CALL", "15 AUG 2026", 2.80, 2.95, 2.88, 28400, 25.1),
                OptionContract(250.0, "CALL", "15 AUG 2026", 1.40, 1.50, 1.45, 42100, 26.2),
                OptionContract(240.0, "PUT", "15 AUG 2026", 2.10, 2.25, 2.18, 18200, 24.8),
                OptionContract(235.0, "PUT", "15 AUG 2026", 1.05, 1.15, 1.10, 22100, 25.8)
            ),
            ownership = mapOf(
                "Vanguard Group Inc." to 8.42,
                "BlackRock Inc." to 6.85,
                "Berkshire Hathaway Inc." to 5.21,
                "State Street Corp." to 3.74,
                "Geode Capital Management" to 2.15
            ),
            peers = listOf("MSFT US", "NVDA US", "GOOGL US", "AMZN US", "META US"),
            analystRatings = mapOf("Buy" to 28, "Outperform" to 8, "Hold" to 6, "Underperform" to 1)
        )
    }

    fun getPortfolioAnalytics(): PortfolioAnalytics {
        return PortfolioAnalytics(
            totalValue = 4821800.0,
            dayChangeDollar = 40280.0,
            dayChangePct = 0.84,
            totalReturnDollar = 842100.0,
            totalReturnPct = 21.16,
            assetAllocation = mapOf(
                "Equities" to 0.48f,
                "Bonds" to 0.27f,
                "Cash" to 0.08f,
                "Commodities" to 0.07f,
                "Other" to 0.10f
            ),
            riskLevel = "Moderate",
            volatilityPct = 14.2,
            beta = 1.08,
            sharpeRatio = 1.85,
            maxDrawdownPct = -8.4,
            varSimulated95Pct = 84200.0,
            sectorExposure = mapOf(
                "Technology" to 0.42f,
                "Fixed Income" to 0.27f,
                "Energy/Commodities" to 0.12f,
                "Financials" to 0.11f,
                "Healthcare" to 0.08f
            ),
            geographicExposure = mapOf(
                "North America" to 0.72f,
                "Europe" to 0.18f,
                "Emerging Markets" to 0.10f
            ),
            currencyExposure = mapOf(
                "USD" to 0.78f,
                "EUR" to 0.12f,
                "GBP" to 0.06f,
                "JPY" to 0.04f
            )
        )
    }

    fun getPortfolioHoldings(): List<PortfolioHolding> {
        return listOf(
            PortfolioHolding("AAPL US", "Apple Inc.", AssetClass.EQUITIES, 4200.0, 185.20, 242.80, 31.10, 21.15, "Technology"),
            PortfolioHolding("MSFT US", "Microsoft Corp.", AssetClass.EQUITIES, 2100.0, 380.00, 448.50, 18.02, 19.54, "Technology"),
            PortfolioHolding("NVDA US", "NVIDIA Corp.", AssetClass.EQUITIES, 3500.0, 92.40, 138.20, 49.56, 10.03, "Semiconductors"),
            PortfolioHolding("US10Y", "US 10-Yr Treasury Note", AssetClass.GOVT_DEBT, 12000.0, 98.50, 101.20, 2.74, 25.18, "Government Debt"),
            PortfolioHolding("XAUUSD", "Gold Bullion Spot", AssetClass.COMMODITIES, 120.0, 2150.00, 2740.50, 27.46, 6.82, "Commodities"),
            PortfolioHolding("BTCUSD", "Bitcoin Vault", AssetClass.CRYPTO, 3.2, 62000.00, 118420.00, 91.00, 7.86, "Digital Assets")
        )
    }

    fun getEconomicIndicators(): List<EconomicIndicator> {
        return listOf(
            EconomicIndicator("US", "US CPI", "Consumer Price Index YoY", "3.1%", "3.4%", "3.2%", "YoY %", "12 AUG 2026", "RELEASED"),
            EconomicIndicator("UK", "UK CPI", "UK CPI YoY Inflation", "2.4%", "2.6%", "2.5%", "YoY %", "10 AUG 2026", "RELEASED"),
            EconomicIndicator("EU", "EU CPI", "Eurozone Harmonised CPI", "2.0%", "2.2%", "2.1%", "YoY %", "11 AUG 2026", "RELEASED"),
            EconomicIndicator("US", "US GDP", "Gross Domestic Product Q2", "2.4%", "2.1%", "2.3%", "Annualized %", "08 AUG 2026", "RELEASED"),
            EconomicIndicator("UK", "UK GDP", "UK GDP Growth QoQ", "1.3%", "1.0%", "1.2%", "QoQ %", "05 AUG 2026", "RELEASED"),
            EconomicIndicator("US", "FED RATE", "Federal Funds Target Rate", "4.25%", "4.50%", "4.25%", "Upper Bound %", "FOMC Meeting", "POLICY"),
            EconomicIndicator("UK", "BOE RATE", "Bank of England Base Rate", "3.75%", "4.00%", "3.75%", "Base Rate %", "MPC Meeting", "POLICY"),
            EconomicIndicator("EU", "ECB RATE", "ECB Main Refinancing Rate", "2.50%", "2.75%", "2.50%", "Policy Rate %", "Governing Council", "POLICY")
        )
    }

    fun getYieldCurvePoints(): List<YieldPoint> {
        return listOf(
            YieldPoint("1M", 0.08, 4.42),
            YieldPoint("3M", 0.25, 4.38),
            YieldPoint("6M", 0.50, 4.28),
            YieldPoint("1Y", 1.00, 4.15),
            YieldPoint("2Y", 2.00, 4.12),
            YieldPoint("5Y", 5.00, 4.15),
            YieldPoint("10Y", 10.00, 4.21),
            YieldPoint("30Y", 30.00, 4.45)
        )
    }

    fun getCorporateBonds(): List<FixedIncomeBond> {
        return listOf(
            FixedIncomeBond("Apple Inc 4.65% 2034", "AAPL34", "Apple Inc", 4.65, "15 MAY 2034", 102.40, 4.35, 6.8, "AAA", 42),
            FixedIncomeBond("Microsoft 4.20% 2035", "MSFT35", "Microsoft Corp", 4.20, "01 JUN 2035", 99.80, 4.22, 7.2, "AAA", 38),
            FixedIncomeBond("Goldman Sachs 5.15% 2032", "GS32", "Goldman Sachs", 5.15, "15 OCT 2032", 104.10, 4.82, 5.4, "A+", 88),
            FixedIncomeBond("JPMorgan Chase 5.50% 2030", "JPM30", "JPMorgan Chase", 5.50, "12 MAR 2030", 105.80, 4.70, 3.9, "A-", 76)
        )
    }

    fun getFXPairs(): List<FXPair> {
        return listOf(
            FXPair("EUR/USD", 1.1842, 0.27, 1.1890, 1.1810, 57.6),
            FXPair("GBP/USD", 1.3721, -0.13, 1.3760, 1.3690, 11.9),
            FXPair("USD/JPY", 148.42, 0.30, 149.10, 147.90, 13.6),
            FXPair("USD/CNY", 7.0842, -0.08, 7.1020, 7.0780, 0.0),
            FXPair("USD/CHF", 0.8840, -0.15, 0.8870, 0.8820, 4.2),
            FXPair("AUD/USD", 0.6780, 0.42, 0.6810, 0.6740, 0.0),
            FXPair("DXY INDEX", 101.42, 0.31, 101.80, 101.10, 100.0)
        )
    }

    fun getCommodities(): List<CommodityItem> {
        return listOf(
            CommodityItem("BRENT", "Brent Crude Oil", 78.40, 1.42, "USD/bbl", "Energy", 79.10, 77.20),
            CommodityItem("WTI", "WTI Crude Oil", 74.80, 1.28, "USD/bbl", "Energy", 75.40, 73.80),
            CommodityItem("NG1", "Natural Gas", 2.85, -2.10, "USD/MMBtu", "Energy", 2.95, 2.80),
            CommodityItem("XAU", "Gold Spot", 2740.50, 0.67, "USD/oz", "Precious Metals", 2755.00, 2722.00),
            CommodityItem("XAG", "Silver Spot", 32.40, 1.41, "USD/oz", "Precious Metals", 33.10, 31.80),
            CommodityItem("HG1", "Copper Futures", 4.35, 0.85, "USD/lb", "Industrial Metals", 4.42, 4.28),
            CommodityItem("W1", "Wheat Futures", 584.20, -0.40, "USd/bu", "Agriculture", 592.00, 580.00),
            CommodityItem("C1", "Corn Futures", 422.50, 0.12, "USd/bu", "Agriculture", 428.00, 419.00),
            CommodityItem("LIT", "Battery Lithium Spot", 14200.0, 3.40, "USD/MT", "EV Materials", 14500.0, 13800.0)
        )
    }

    fun getEarningsCalendar(): List<EarningsEvent> {
        return listOf(
            EarningsEvent("AAPL US", "Apple Inc.", "16:30 EST", "Est: $1.48", "Actual: $1.52", "BEAT", "iPhone 16 upgrade cycle & Services revenue hit record $24.2B"),
            EarningsEvent("MSFT US", "Microsoft Corp.", "17:00 EST", "Est: $3.10", "Actual: $3.18", "BEAT", "Azure Cloud grew 29% YoY; Enterprise AI adoption accelerating"),
            EarningsEvent("NVDA US", "NVIDIA Corp.", "After Close", "Est: $0.68", null, "UPCOMING", "Analysts looking for Blackwell B200 chip shipment guidance"),
            EarningsEvent("AMZN US", "Amazon.com Inc.", "Tomorrow", "Est: $1.14", null, "UPCOMING", "AWS growth & retail margin compression in focus"),
            EarningsEvent("TSLA US", "Tesla Inc.", "Thursday", "Est: $0.54", null, "UPCOMING", "Full Self-Driving (FSD) v13 & Robotaxi fleet timeline")
        )
    }

    suspend fun toggleWatchlist(ticker: String, name: String, assetClass: String) {
        val existing = dao.getWatchlist().firstOrNull()?.find { it.ticker == ticker }
        if (existing != null) {
            dao.deleteWatchlist(ticker)
        } else {
            dao.insertWatchlist(WatchlistEntity(ticker, name, assetClass))
        }
    }

    suspend fun addAlert(ticker: String, title: String, condition: String, thresholdValue: String, category: String) {
        val alert = AlertEntity(
            id = UUID.randomUUID().toString(),
            ticker = ticker,
            title = title,
            condition = condition,
            thresholdValue = thresholdValue,
            category = category,
            isTriggered = false,
            triggeredTime = ""
        )
        dao.insertAlert(alert)
    }

    suspend fun deleteAlert(id: String) {
        dao.deleteAlert(id)
    }

    suspend fun sendChatMessage(text: String, sharedTicker: String? = null) {
        val newMsg = CollabMessage(
            sender = "User Portfolio Mgr",
            role = "Institutional Trader",
            text = text,
            timestampStr = dateFormat.format(Date()),
            sharedTicker = sharedTicker
        )
        _chatMessages.value = _chatMessages.value + newMsg
    }
}
