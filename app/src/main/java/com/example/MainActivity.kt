package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.*
import com.example.ui.components.TerminalTopBar
import com.example.ui.components.TickerSearchDialog
import com.example.ui.screens.*
import com.example.ui.theme.BloombergTheme
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MarketViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloombergTheme {
                BloombergMainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloombergMainApp(viewModel: MarketViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isMobileMode by viewModel.isMobileMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchOpen by viewModel.isSearchOpen.collectAsStateWithLifecycle()
    val selectedTicker by viewModel.selectedSecurityTicker.collectAsStateWithLifecycle()

    val marketItems by viewModel.marketItems.collectAsStateWithLifecycle()
    val filteredMarketItems by viewModel.filteredMarketItems.collectAsStateWithLifecycle()
    val newsFeed by viewModel.newsFeed.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val watchlist by viewModel.watchlist.collectAsStateWithLifecycle()
    val userAlerts by viewModel.userAlerts.collectAsStateWithLifecycle()
    val securityDetail by viewModel.currentSecurityDetail.collectAsStateWithLifecycle()

    val selectedAssetClass by viewModel.selectedAssetClass.collectAsStateWithLifecycle()
    val selectedNewsCategory by viewModel.selectedNewsCategory.collectAsStateWithLifecycle()
    val chartType by viewModel.chartType.collectAsStateWithLifecycle()
    val showSMA by viewModel.showSMA.collectAsStateWithLifecycle()
    val showRSI by viewModel.showRSI.collectAsStateWithLifecycle()
    val showMACD by viewModel.showMACD.collectAsStateWithLifecycle()
    val showBollinger by viewModel.showBollinger.collectAsStateWithLifecycle()
    val overlayTicker by viewModel.overlayTicker.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TerminalBlack,
        topBar = {
            TerminalTopBar(
                marketItems = marketItems,
                isMobileMode = isMobileMode,
                alertCount = userAlerts.size,
                onToggleMobileMode = { viewModel.toggleMobileMode() },
                onOpenSearch = { viewModel.toggleSearch(true) },
                onOpenAlerts = { viewModel.setTab(MainTab.WATCHLIST) },
                onSelectTicker = { ticker -> viewModel.selectSecurity(ticker) }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TerminalHeader)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // High Density Secondary Tab Bar Quick Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalSurfaceVariant)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        Surface(
                            color = if (isSelected) TerminalAmber else Color.Transparent,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier
                                .clickable { viewModel.setTab(tab) }
                                .padding(horizontal = 2.dp)
                        ) {
                            Text(
                                text = tab.displayName.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isSelected) Color.Black else TerminalTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Divider(color = TerminalBorder, thickness = 1.dp)

                // Primary Bloomberg Navigation Bar
                NavigationBar(
                    containerColor = TerminalHeader,
                    contentColor = TerminalTextSecondary,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(56.dp)
                        .testTag("bloomberg_bottom_navigation")
                ) {
                    val primaryTabs = listOf(
                        MainTab.HOME to (Icons.Default.Home to "Home"),
                        MainTab.MARKETS to (Icons.Default.TrendingUp to "Markets"),
                        MainTab.NEWS to (Icons.Default.Newspaper to "News"),
                        MainTab.PORTFOLIO to (Icons.Default.PieChart to "Portfolio"),
                        MainTab.AI to (Icons.Default.Psychology to "AI"),
                        MainTab.WORKSPACE to (Icons.Default.Dashboard to "Workspace")
                    )

                    primaryTabs.forEach { (tab, iconAndLabel) ->
                        val (icon, label) = iconAndLabel
                        val isSelected = currentTab == tab

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label.uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TerminalAmber,
                                selectedTextColor = TerminalAmber,
                                unselectedIconColor = TerminalTextMuted,
                                unselectedTextColor = TerminalTextMuted,
                                indicatorColor = TerminalSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TerminalBlack)
        ) {
            when (currentTab) {
                MainTab.HOME -> HomeMarketCentreScreen(
                    marketItems = marketItems,
                    topNews = newsFeed,
                    portfolio = viewModel.getPortfolioAnalytics(),
                    onSelectTicker = { viewModel.selectSecurity(it) },
                    onNavigateTab = { viewModel.setTab(it) },
                    onAskAiPrompt = { viewModel.askAi(it) }
                )

                MainTab.MARKETS -> MarketsCenterScreen(
                    marketItems = filteredMarketItems,
                    selectedAssetClass = selectedAssetClass,
                    watchlistTickers = watchlist.map { it.ticker },
                    onSelectAssetClass = { viewModel.setSelectedAssetClass(it) },
                    onSelectTicker = { viewModel.selectSecurity(it) },
                    onToggleWatchlist = { ticker, name, cls -> viewModel.toggleWatchlist(ticker, name, cls) }
                )

                MainTab.NEWS -> NewsTerminalScreen(
                    newsArticles = newsFeed,
                    selectedCategory = selectedNewsCategory,
                    onSelectCategory = { viewModel.setSelectedNewsCategory(it) },
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.PORTFOLIO -> PortfolioDashboardScreen(
                    portfolio = viewModel.getPortfolioAnalytics(),
                    holdings = viewModel.getPortfolioHoldings(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.RESEARCH -> SecurityTerminalScreen(
                    security = securityDetail,
                    chartPrices = marketItems.find { it.ticker == securityDetail.ticker }?.history ?: emptyList(),
                    chartType = chartType,
                    showSMA = showSMA,
                    showRSI = showRSI,
                    showMACD = showMACD,
                    showBollinger = showBollinger,
                    overlayTicker = overlayTicker,
                    onChartTypeChange = { viewModel.setChartType(it) },
                    onToggleSMA = { viewModel.toggleSMA() },
                    onToggleRSI = { viewModel.toggleRSI() },
                    onToggleMACD = { viewModel.toggleMACD() },
                    onToggleBollinger = { viewModel.toggleBollinger() },
                    onOverlayChange = { viewModel.setOverlayTicker(it) },
                    onSelectPeer = { viewModel.selectSecurity(it) }
                )

                MainTab.AI -> AiResearchAssistantScreen(
                    aiMessages = aiMessages,
                    isLoading = isAiLoading,
                    onSendMessage = { viewModel.askAi(it) }
                )

                MainTab.ECONOMICS -> EconomicsMacroScreen(
                    indicators = viewModel.getEconomicIndicators(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.FIXED_INCOME -> FixedIncomeScreen(
                    yieldPoints = viewModel.getYieldCurvePoints(),
                    corporateBonds = viewModel.getCorporateBonds(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.FX -> FxScreen(
                    fxPairs = viewModel.getFXPairs(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.COMMODITIES -> CommoditiesScreen(
                    commodities = viewModel.getCommodities(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.WATCHLIST -> WatchlistsAlertsScreen(
                    watchlist = watchlist,
                    alerts = userAlerts,
                    onSelectTicker = { viewModel.selectSecurity(it) },
                    onDeleteWatchlist = { viewModel.toggleWatchlist(it, "", "") },
                    onAddAlert = { ticker, title, condition, valStr, cat ->
                        viewModel.addAlert(ticker, title, condition, valStr, cat)
                    },
                    onDeleteAlert = { viewModel.deleteAlert(it) }
                )

                MainTab.EARNINGS -> EarningsCentreScreen(
                    earningsEvents = viewModel.getEarningsCalendar(),
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.WORKSPACE -> TerminalWorkspaceScreen(
                    marketItems = marketItems,
                    news = newsFeed,
                    portfolio = viewModel.getPortfolioAnalytics(),
                    chartPrices = marketItems.find { it.ticker == securityDetail.ticker }?.history ?: emptyList(),
                    chartType = chartType,
                    onSelectTicker = { viewModel.selectSecurity(it) }
                )

                MainTab.COLLAB -> CollaborationScreen(
                    chatMessages = chatMessages,
                    onSendMessage = { viewModel.sendChatMessage(it) }
                )
            }

            if (isSearchOpen) {
                TickerSearchDialog(
                    searchQuery = searchQuery,
                    filteredItems = filteredMarketItems,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSelectTicker = { viewModel.selectSecurity(it) },
                    onDismiss = { viewModel.toggleSearch(false) }
                )
            }
        }
    }
}
