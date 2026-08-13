package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.api.GeminiClient
import com.example.data.local.AlertEntity
import com.example.data.local.TerminalDatabase
import com.example.data.local.WatchlistEntity
import com.example.data.model.*
import com.example.data.repository.MarketRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class MainTab(val displayName: String, val iconName: String) {
    HOME("Home", "home"),
    MARKETS("Markets", "trending_up"),
    NEWS("News", "newspaper"),
    PORTFOLIO("Portfolio", "pie_chart"),
    RESEARCH("Security", "analytics"),
    AI("AskB AI", "psychology"),
    ECONOMICS("Macro", "public"),
    FIXED_INCOME("Bonds", "account_balance"),
    FX("FX", "currency_exchange"),
    COMMODITIES("Commodities", "oil_barrel"),
    WATCHLIST("Watchlist", "star"),
    EARNINGS("Earnings", "event"),
    WORKSPACE("Workspace", "dashboard"),
    COLLAB("Instant IB", "forum")
}

enum class ChartType {
    CANDLESTICK, LINE, MOUNTAIN, YIELD_CURVE
}

data class AiMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: String = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(java.util.Date()),
    val isGeneratedAnalysis: Boolean = false
)

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        TerminalDatabase::class.java,
        "bloomberg_terminal.db"
    ).build()

    private val repository = MarketRepository(db.terminalDao(), viewModelScope)

    // Navigation & View Mode
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _isMobileMode = MutableStateFlow(true)
    val isMobileMode: StateFlow<Boolean> = _isMobileMode.asStateFlow()

    // Global Search & Security Selection
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _selectedSecurityTicker = MutableStateFlow("AAPL US")
    val selectedSecurityTicker: StateFlow<String> = _selectedSecurityTicker.asStateFlow()

    // Markets Center Filter
    private val _selectedAssetClass = MutableStateFlow<AssetClass?>(null)
    val selectedAssetClass: StateFlow<AssetClass?> = _selectedAssetClass.asStateFlow()

    // News Filter
    private val _selectedNewsCategory = MutableStateFlow<NewsCategory?>(null)
    val selectedNewsCategory: StateFlow<NewsCategory?> = _selectedNewsCategory.asStateFlow()

    // Advanced Charting State
    private val _chartType = MutableStateFlow(ChartType.CANDLESTICK)
    val chartType: StateFlow<ChartType> = _chartType.asStateFlow()

    private val _showSMA = MutableStateFlow(true)
    val showSMA: StateFlow<Boolean> = _showSMA.asStateFlow()

    private val _showRSI = MutableStateFlow(true)
    val showRSI: StateFlow<Boolean> = _showRSI.asStateFlow()

    private val _showMACD = MutableStateFlow(false)
    val showMACD: StateFlow<Boolean> = _showMACD.asStateFlow()

    private val _showBollinger = MutableStateFlow(false)
    val showBollinger: StateFlow<Boolean> = _showBollinger.asStateFlow()

    private val _overlayTicker = MutableStateFlow<String?>(null)
    val overlayTicker: StateFlow<String?> = _overlayTicker.asStateFlow()

    // AI Chat Stream
    private val _aiMessages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                isUser = false,
                text = "Welcome to Bloomberg Intelligence AI (AskB). Ask any market question, portfolio risk query, or comparative research prompt.",
                isGeneratedAnalysis = true
            )
        )
    )
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Workspace Custom Layout Panels
    private val _workspacePanels = MutableStateFlow(
        listOf("MARKETS", "NEWS", "CHART", "PORTFOLIO", "AI")
    )
    val workspacePanels: StateFlow<List<String>> = _workspacePanels.asStateFlow()

    // Data Exports from Repository
    val marketItems = repository.marketItems
    val newsFeed = repository.newsFeed
    val chatMessages = repository.chatMessages
    val watchlist = repository.watchlistTickers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val userAlerts = repository.userAlerts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredMarketItems: StateFlow<List<MarketItem>> = combine(
        marketItems, searchQuery, selectedAssetClass
    ) { items, query, assetClass ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.ticker.contains(query, ignoreCase = true) ||
                    item.name.contains(query, ignoreCase = true)
            val matchesClass = assetClass == null || item.assetClass == assetClass
            matchesQuery && matchesClass
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentSecurityDetail: StateFlow<SecurityDetail> = selectedSecurityTicker.map { ticker ->
        repository.getSecurityDetail(ticker)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.getSecurityDetail("AAPL US")
    )

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun toggleMobileMode() {
        _isMobileMode.value = !_isMobileMode.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearch(open: Boolean? = null) {
        _isSearchOpen.value = open ?: !_isSearchOpen.value
    }

    fun selectSecurity(ticker: String) {
        _selectedSecurityTicker.value = ticker
        _isSearchOpen.value = false
        _currentTab.value = MainTab.RESEARCH
    }

    fun setSelectedAssetClass(assetClass: AssetClass?) {
        _selectedAssetClass.value = assetClass
    }

    fun setSelectedNewsCategory(category: NewsCategory?) {
        _selectedNewsCategory.value = category
    }

    fun setChartType(type: ChartType) {
        _chartType.value = type
    }

    fun toggleSMA() { _showSMA.value = !_showSMA.value }
    fun toggleRSI() { _showRSI.value = !_showRSI.value }
    fun toggleMACD() { _showMACD.value = !_showMACD.value }
    fun toggleBollinger() { _showBollinger.value = !_showBollinger.value }

    fun setOverlayTicker(ticker: String?) {
        _overlayTicker.value = if (_overlayTicker.value == ticker) null else ticker
    }

    fun askAi(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = AiMessage(isUser = true, text = prompt)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiLoading.value = true

        viewModelScope.launch {
            val contextData = "S&P 500: 6,842.12 (+0.42%), NASDAQ: 22,481.30 (+0.71%), BTC: $118,420 (+2.14%), Portfolio Val: $4.82M (+0.84%), Selected Ticker: ${_selectedSecurityTicker.value}"
            val responseText = GeminiClient.askBloombergAI(prompt, contextData)

            _aiMessages.value = _aiMessages.value + AiMessage(
                isUser = false,
                text = responseText,
                isGeneratedAnalysis = true
            )
            _isAiLoading.value = false
        }
    }

    fun toggleWatchlist(ticker: String, name: String, assetClass: String) {
        viewModelScope.launch {
            repository.toggleWatchlist(ticker, name, assetClass)
        }
    }

    fun addAlert(ticker: String, title: String, condition: String, thresholdValue: String, category: String) {
        viewModelScope.launch {
            repository.addAlert(ticker, title, condition, thresholdValue, category)
        }
    }

    fun deleteAlert(id: String) {
        viewModelScope.launch {
            repository.deleteAlert(id)
        }
    }

    fun sendChatMessage(text: String, sharedTicker: String? = null) {
        viewModelScope.launch {
            repository.sendChatMessage(text, sharedTicker)
        }
    }

    // Repository Getters
    fun getPortfolioAnalytics() = repository.getPortfolioAnalytics()
    fun getPortfolioHoldings() = repository.getPortfolioHoldings()
    fun getEconomicIndicators() = repository.getEconomicIndicators()
    fun getYieldCurvePoints() = repository.getYieldCurvePoints()
    fun getCorporateBonds() = repository.getCorporateBonds()
    fun getFXPairs() = repository.getFXPairs()
    fun getCommodities() = repository.getCommodities()
    fun getEarningsCalendar() = repository.getEarningsCalendar()
}
