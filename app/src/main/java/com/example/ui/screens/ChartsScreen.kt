package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChartEntry
import com.example.ui.ChartViewModel
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkSlateActive
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.DiamondCert
import com.example.ui.theme.GoldCert
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlatinumCert
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.GlassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.CustomOutline

@Composable
fun ChartsScreen(
    viewModel: ChartViewModel,
    innerPadding: PaddingValues
) {
    val charts by viewModel.charts.collectAsState()
    val selectedChartType by viewModel.selectedChartType.collectAsState()
    val selectedTimeframe by viewModel.selectedTimeframe.collectAsState()
    val selectedEntityType by viewModel.selectedEntityType.collectAsState()
    val selectedSize by viewModel.selectedChartSize.collectAsState()
    val availableTimeframes by viewModel.availableTimeframes.collectAsState()

    var expandedItemId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(innerPadding)
            .testTag("charts_screen")
    ) {
        // 1. TIMEFRAME tabs
        TimeframeTabs(
            selectedType = selectedChartType,
            onTypeSelected = { viewModel.setChartType(it) }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 2. CHART FILTERS GRID (SONGS, ALBUMS, ARTISTS & CHOSEN SIZE)
            item {
                ChartFiltersRow(
                    selectedEntity = selectedEntityType,
                    selectedSize = selectedSize,
                    onEntitySelected = { viewModel.setEntityType(it) },
                    onSizeSelected = { viewModel.setChartSize(it) }
                )
            }

            // 3. PAGINATION TIME BLOCK
            item {
                TimeframePager(
                    selectedTimeframe = selectedTimeframe,
                    timeframes = availableTimeframes,
                    onTimeframeChanged = { viewModel.setTimeframe(it) }
                )
            }

            // 4. COLUMN LABELS
            item {
                ColumnHeaderLabels()
            }

            // EMPTY STATE PROMPT IF CHART HAS NO ITEMS
            if (charts.isEmpty()) {
                item {
                    EmptyChartState()
                }
            } else {
                items(charts) { entry ->
                    ChartRowItem(
                        entry = entry,
                        isExpanded = expandedItemId == entry.id,
                        onClick = {
                            expandedItemId = if (expandedItemId == entry.id) null else entry.id
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TimeframeTabs(
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    val tabs = listOf("WEEKLY", "MONTHLY", "YEARLY", "ALL-TIME")
    val selectedIndex = tabs.indexOf(selectedType).coerceAtLeast(0)

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = DarkSlateSurface,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth().testTag("timeframe_tabs_row")
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTypeSelected(title) },
                text = {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (selectedIndex == index) NeonCyan else PlatinumText.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.testTag("tab_${title.lowercase().replace("-", "_")}")
            )
        }
    }
}

@Composable
fun ChartFiltersRow(
    selectedEntity: String,
    selectedSize: Int,
    onEntitySelected: (String) -> Unit,
    onSizeSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSlateSurface, RoundedCornerShape(8.dp))
            .border(1.dp, CustomOutline, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        // Entity Select Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("SONG", "ARTIST", "ALBUM").forEach { entity ->
                val isSelected = selectedEntity == entity
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isSelected) AccentBlue else DarkSlateActive,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onEntitySelected(entity) }
                        .padding(vertical = 8.dp)
                        .testTag("filter_btn_${entity.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${entity}S",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else PlatinumText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Size Row
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(10, 20, 30, 50, 100).forEach { size ->
                val isSelected = selectedSize == size
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) NeonCyan else DarkSlateActive,
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { onSizeSelected(size) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("size_btn_$size"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Top $size",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) DeepNavyBg else Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TimeframePager(
    selectedTimeframe: String,
    timeframes: List<String>,
    onTimeframeChanged: (String) -> Unit
) {
    val currentIndex = timeframes.indexOf(selectedTimeframe)
    val hasPrev = currentIndex < timeframes.size - 1
    val hasNext = currentIndex > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                if (hasPrev) onTimeframeChanged(timeframes[currentIndex + 1])
            },
            enabled = hasPrev,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkSlateSurface,
                disabledContainerColor = DarkSlateSurface.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.height(30.dp).testTag("prev_timeframe_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = if (hasPrev) NeonCyan else PlatinumText.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
                Text("Prev", fontSize = 11.sp, color = if (hasPrev) Color.White else PlatinumText.copy(alpha = 0.3f))
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Active Period",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = selectedTimeframe,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = {
                if (hasNext) onTimeframeChanged(timeframes[currentIndex - 1])
            },
            enabled = hasNext,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkSlateSurface,
                disabledContainerColor = DarkSlateSurface.copy(alpha = 0.5f)
            ),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.height(30.dp).testTag("next_timeframe_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Next", fontSize = 11.sp, color = if (hasNext) Color.White else PlatinumText.copy(alpha = 0.3f))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = if (hasNext) NeonCyan else PlatinumText.copy(alpha = 0.3f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ColumnHeaderLabels() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "#", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(36.dp))
        Text(text = "TITLE - ARTIST", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
        Text(text = "CHANGE", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(55.dp), textAlign = TextAlign.Center)
        Text(text = "WKS", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(42.dp), textAlign = TextAlign.Center)
        Text(text = "PLAYS", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(55.dp), textAlign = TextAlign.End)
    }
}

@Composable
fun ChartRowItem(
    entry: ChartEntry,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isExpanded) AccentBlue else GlassBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("chart_item_${entry.rank}"),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RATING RANK
                Box(
                    modifier = Modifier.width(36.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = entry.rank.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (entry.rank == 1) GoldCert else Color.White
                    )
                }

                // DATA INFORMATION COLUMN
                Column(modifier = Modifier.weight(1f).padding(end = 4.dp)) {
                    Text(
                        text = entry.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (entry.artist.isNotBlank()) {
                        Text(
                            text = entry.artist,
                            fontSize = 10.sp,
                            color = PlatinumText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Badges indicators
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Peak label tag
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF1D2E44), RoundedCornerShape(2.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PEAK #${entry.peakRank}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }

                        // Certification badges
                        if (entry.plays >= 200) {
                            CertBadge("DIAMOND", DiamondCert)
                        } else if (entry.plays >= 100) {
                            CertBadge("PLATINUM", PlatinumCert)
                        } else if (entry.plays >= 50) {
                            CertBadge("GOLD", GoldCert)
                        }
                    }
                }

                // CHANGE INDICATOR
                Box(
                    modifier = Modifier.width(55.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ChangeLabel(entry.changeIndicator)
                }

                // WEEKS ON CHART
                Box(
                    modifier = Modifier.width(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = entry.weeksOnChart.toString(),
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // PLAYS
                Box(
                    modifier = Modifier.width(55.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${entry.plays}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan
                        )
                        Text(text = "plays", fontSize = 8.sp, color = PlatinumText.copy(alpha = 0.4f))
                    }
                }
            }

            // EXPANDED DETAIL SECTION
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ExpanderSection(entry)
            }
        }
    }
}

@Composable
fun CertBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
            .border(1.dp, color, RoundedCornerShape(2.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun ChangeLabel(change: String) {
    val color = when {
        change.startsWith("^") -> Color(0xFF39D353)
        change.startsWith("v") -> Color(0xFFF13C4A)
        change == "NEW" -> Color(0xFF1B85FF)
        change == "RE" -> Color(0xFF9042F3)
        else -> PlatinumText.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = change,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            color = color
        )
    }
}

@Composable
fun ExpanderSection(entry: ChartEntry) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSlateActive)
            .padding(12.dp)
    ) {
        Divider(color = CustomOutline, thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        // subtitle title
        Text(
            text = "📊 CHART RUN HISTORY",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Row of previous chart run points
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Apr 26" to "#5",
                "May 3" to "#1",
                "May 10" to "#2",
                "May 17" to "#4"
            ).forEach { (date, rank) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    modifier = Modifier.border(1.dp, CustomOutline, RoundedCornerShape(4.dp))
                ) {
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = date, fontSize = 8.sp, color = PlatinumText)
                        Text(text = rank, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LISTENING HEATMAP TITLE
        Text(
            text = "🔥 LISTENING DENSITY HEATMAP",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Heatmap squares representing scrobble density
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(
                Pair(Color(0xFF172C46), "Mon"), Pair(Color(0xFF4C1080), "Tue"),
                Pair(Color(0xFF7A1FA2), "Wed"), Pair(Color(0xFFC2185B), "Thu"),
                Pair(Color(0xFFE91E63), "Fri"), Pair(Color(0xFFFF2A85), "Sat"),
                Pair(Color(0xFFFF6EB3), "Sun")
            ).forEach { (densityColor, day) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(densityColor)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = day, fontSize = 7.sp, color = PlatinumText)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // BUTTON SHARES
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = CustomOutline),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube Video", fontSize = 10.sp, color = Color.White)
                }
            }
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = CustomOutline),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share Stats", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun EmptyChartState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No recorded charts found",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Add new scrobbles or try syncing to compile high-fidelity user ranks.",
            fontSize = 11.sp,
            color = PlatinumText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp)
        )
    }
}
