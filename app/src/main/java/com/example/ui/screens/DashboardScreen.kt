package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChartViewModel
import com.example.ui.theme.DarkSlateActive
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.GlassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.CustomOutline
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: ChartViewModel,
    innerPadding: PaddingValues
) {
    val profile by viewModel.userProfile.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(innerPadding)
                .testTag("dashboard_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. BRAND HEADER
            item {
                BrandHeader(onSettingsClick = { showSettingsDialog = true })
            }

        // 2. MAIN CORE STATS
        item {
            profile?.let { prof ->
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 3
                ) {
                    val widthModifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)
                    
                    StatCard(
                        title = "TOTAL PLAYS",
                        value = NumberFormat.getNumberInstance(Locale.US).format(prof.totalPlays),
                        icon = Icons.Default.MusicNote,
                        subText = "plays loaded",
                        modifier = widthModifier.testTag("stat_total_plays")
                    )
                    StatCard(
                        title = "DAYS LISTENED",
                        value = NumberFormat.getNumberInstance(Locale.US).format(prof.daysListened),
                        icon = Icons.Default.DateRange,
                        subText = "active days",
                        modifier = widthModifier.testTag("stat_days_listened")
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 3
                ) {
                    val widthModifier = Modifier
                        .weight(1f)
                        .padding(bottom = 10.dp)

                    StatCard(
                        title = "PLAYS / DAY",
                        value = prof.playsPerDay.toString(),
                        icon = Icons.Default.Speed,
                        subText = "average rate",
                        modifier = widthModifier.testTag("stat_plays_per_day")
                    )
                    StatCard(
                        title = "TOP ARTIST",
                        value = prof.topArtist,
                        icon = Icons.Default.Mic,
                        subText = "all-time headliner",
                        modifier = widthModifier.testTag("stat_top_artist")
                    )
                    StatCard(
                        title = "DAY STREAK",
                        value = "${prof.dayStreak} 🔥",
                        icon = Icons.Default.LocalFireDepartment,
                        subText = "PB: ${prof.pbStreak} days",
                        modifier = widthModifier.testTag("stat_day_streak")
                    )
                }
            }
        }

        // 3. SYNCHRONIZE & CONFIGURE STATUS BAR
        item {
            SyncBar(
                viewModel = viewModel,
                isSyncing = isSyncing,
                username = profile?.username ?: "Guest"
            )
        }

        // 4. SUMMARY ROW BRIEF BANNER
        item {
            DankBanner()
        }

        // 5. UPCOMING / RECENT MUSIC RELEASES GRID (Simulating 1-to-1 Web Releases Grid)
        item {
            Text(
                text = "✦ UPCOMING & RECENT RELEASES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    ReleaseItemData("SS26", "Charli xcx", "SINGLE", Color(0xFFF13C4A)),
                    ReleaseItemData("the cure", "Olivia Rodrigo", "SINGLE", Color(0xFF704EF3)),
                    ReleaseItemData("Live from Mexico", "Dua Lipa", "ALBUM", Color(0xFF00D2FF)),
                    ReleaseItemData("hate that i made you...", "Ariana Grande", "SINGLE", Color(0xFFE288B3)),
                    ReleaseItemData("Sanctuary", "Evanescence", "ALBUM", Color(0xFF3B566E)),
                    ReleaseItemData("DIRTY BLONDE", "Bebe Rexha", "ALBUM", Color(0xFFF9D131))
                ).forEach { release ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .widthIn(min = 150.dp, max = 200.dp)
                            .padding(bottom = 12.dp)
                    ) {
                        ReleaseCard(release)
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showSettingsDialog = false }
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepNavyBg),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                        .border(1.dp, CustomOutline, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    com.example.ui.screens.SettingsScreen(
                        viewModel = viewModel,
                        onDismissOrComplete = { showSettingsDialog = false }
                    )
                }
            }
        }
    }
}

@Composable
fun BrandHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SYNC ACTIVE",
                fontSize = 10.sp,
                color = AccentBlue,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "DANKCHARTS",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = ".FM",
                    fontSize = 24.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black,
                    color = AccentBlue,
                    letterSpacing = (-0.5).sp
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(32.dp).testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Avatar representation matching CSS theme's upper right element
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(AccentBlue, Color(0xFF1E3A8A))
                        )
                    )
                    .border(1.dp, AccentBlue.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    subText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PlatinumText.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                fontSize = 9.sp,
                color = PlatinumText.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SyncBar(
    viewModel: ChartViewModel,
    isSyncing: Boolean,
    username: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF39D353),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Synced • $username",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
                
                Button(
                    onClick = { viewModel.syncNow() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("sync_now_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSyncing) "Syncing..." else "SYNC NOW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            if (isSyncing) {
                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = NeonCyan,
                    trackColor = DeepNavyBg
                )
            }
        }
    }
}

@Composable
fun DankBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF04101D),
                        Color(0xFF1B3B5F)
                    )
                )
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFFD43F).copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = Color(0xFFFFD43F),
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "THE CERTIFICATIONS WALL IS LIVE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Track your gold, platinum, and diamond record milestones.",
                    fontSize = 10.sp,
                    color = PlatinumText.copy(alpha = 0.7f)
                )
            }
        }
    }
}

data class ReleaseItemData(
    val title: String,
    val artist: String,
    val type: String,
    val cardColor: Color
)

@Composable
fun ReleaseCard(release: ReleaseItemData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CustomOutline, RoundedCornerShape(6.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(release.cardColor, release.cardColor.copy(alpha = 0.3f))
                        )
                    )
                    .drawBehind {
                        // Drawing retro cover grids lines
                        val stepSize = maxOf(20.dp.toPx().toInt(), 1)
                        for (x in 0..this.size.width.toInt() step stepSize) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.05f),
                                start = Offset(x.toFloat(), 0f),
                                end = Offset(x.toFloat(), this.size.height)
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                )
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = release.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = release.artist,
                    fontSize = 10.sp,
                    color = PlatinumText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1D2E44), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = release.type,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                }
            }
        }
    }
}
