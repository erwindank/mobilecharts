package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChartViewModel
import com.example.ui.screens.CertificationsScreen
import com.example.ui.screens.ChartsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.RawDataScreen
import com.example.ui.screens.ShareProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkSlateActive
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.AccentBlue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout()
            }
        }
    }
}

enum class MainNavigationTab(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
) {
    DASHBOARD("Home", Icons.Default.Home, "nav_dashboard_tab"),
    CHARTS("Charts", Icons.Default.TrendingUp, "nav_charts_tab"),
    CERTIFICATIONS("Plaques", Icons.Default.WorkspacePremium, "nav_certifications_tab"),
    RAW_DATA("Raw Data", Icons.Default.ListAlt, "nav_raw_data_tab"),
    SHARE_PROFILE("Share App", Icons.Default.Share, "nav_share_tab")
}

@Composable
fun MainAppLayout() {
    val viewModel: ChartViewModel = viewModel()
    val profile by viewModel.userProfile.collectAsState()
    var currentTab by remember { mutableStateOf(MainNavigationTab.DASHBOARD) }

    if (profile != null && !profile!!.onboardingCompleted) {
        SettingsScreen(
            viewModel = viewModel,
            onDismissOrComplete = {}
        )
        return
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // Draw deep solid background matching immersive spec
                drawRect(DeepNavyBg)
                // Draw modern top-centered radial glow safely
                if (size.width > 0f && size.height > 0f) {
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(AccentBlue.copy(alpha = 0.15f), Color.Transparent),
                            center = Offset(size.width / 2f, 0f),
                            radius = size.height * 0.75f
                        )
                    )
                }
            }
            .testTag("main_scaffold"),
        bottomBar = {
            NavigationBar(
                containerColor = DarkSlateSurface,
                modifier = Modifier.testTag("app_navigation_bar"),
                tonalElevation = 8.dp
            ) {
                MainNavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            indicatorColor = DarkSlateActive,
                            unselectedIconColor = Color.White.copy(alpha = 0.4f),
                            unselectedTextColor = Color.White.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            MainNavigationTab.DASHBOARD -> DashboardScreen(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
            MainNavigationTab.CHARTS -> ChartsScreen(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
            MainNavigationTab.CERTIFICATIONS -> CertificationsScreen(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
            MainNavigationTab.RAW_DATA -> RawDataScreen(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
            MainNavigationTab.SHARE_PROFILE -> ShareProfileScreen(
                viewModel = viewModel,
                innerPadding = innerPadding
            )
        }
    }
}
