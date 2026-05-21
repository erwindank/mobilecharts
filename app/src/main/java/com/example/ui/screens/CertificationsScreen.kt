package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Certification
import com.example.ui.ChartViewModel
import com.example.ui.theme.DarkSlateActive
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.DiamondCert
import com.example.ui.theme.GoldCert
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlatinumCert
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.TitleWhite
import com.example.ui.theme.TripleDiamondCert
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.GlassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.CustomOutline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CertificationsScreen(
    viewModel: ChartViewModel,
    innerPadding: PaddingValues
) {
    val certs by viewModel.certifications.collectAsState()
    var filterType by remember { mutableStateOf("ALL") } // ALL, SONG, ALBUM

    val filteredCerts = remember(certs, filterType) {
        if (filterType == "ALL") certs else certs.filter { it.entityType == filterType }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(innerPadding)
            .testTag("certifications_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HEADER SECTION
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "THE CERTIFICATIONS WALL",
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = TitleWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track your record milestones. Achievements are certified once listening play counts cross critical volume thresholds.",
                    fontSize = 11.sp,
                    color = PlatinumText.copy(alpha = 0.8f)
                )
            }
        }

        // 2. SUMMARY COUNTER METRICS
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassBg, RoundedCornerShape(16.dp))
                    .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CertSummaryCell(label = "DIAMOND", count = certs.count { it.certType.contains("DIAMOND") }, color = DiamondCert)
                CertSummaryCell(label = "PLATINUM", count = certs.count { it.certType == "PLATINUM" }, color = PlatinumCert)
                CertSummaryCell(label = "GOLD", count = certs.count { it.certType == "GOLD" }, color = GoldCert)
            }
        }

        // 3. SELECTION FILTERS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "ALL" to "All Records",
                    "SONG" to "Songs Only",
                    "ALBUM" to "Albums Only"
                ).forEach { (type, label) ->
                    val isSelected = filterType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) AccentBlue.copy(alpha = 0.2f) else GlassBg,
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, if (isSelected) AccentBlue else GlassBorder, RoundedCornerShape(12.dp))
                            .clickable { filterType = type }
                            .padding(vertical = 8.dp)
                            .testTag("cert_filter_$type"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 4. METALLIC PLAQUES LISTING
        if (filteredCerts.isEmpty()) {
            item {
                EmptyCertState()
            }
        } else {
            items(filteredCerts) { cert ->
                PlaqueCard(cert)
            }
        }
    }
}

@Composable
fun CertSummaryCell(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.sp
        )
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = "certified",
            fontSize = 8.sp,
            color = PlatinumText.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun PlaqueCard(cert: Certification) {
    val gradientBrush = when (cert.certType) {
        "TRIPLE_DIAMOND" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121))
        )
        "DIAMOND" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF2193B0), Color(0xFF6DDFFA))
        )
        "PLATINUM" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF808A9F), Color(0xFFBDC3C7), Color(0xFF7F8C8D))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFFE5A93B), Color(0xFFFFF1AD), Color(0xFFBF8017))
        )
    }

    val plaqueOutlineColor = when (cert.certType) {
        "TRIPLE_DIAMOND" -> TripleDiamondCert
        "DIAMOND" -> DiamondCert
        "PLATINUM" -> PlatinumCert
        else -> GoldCert
    }

    val icon = if (cert.entityType == "SONG") Icons.Default.MusicNote else Icons.Default.Album
    val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(cert.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, plaqueOutlineColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .testTag("plaque_${cert.certType.lowercase()}_${cert.id}"),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shiny Metallic plaque container
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(gradientBrush),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = plaqueOutlineColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text descriptions
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(plaqueOutlineColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                            .border(1.dp, plaqueOutlineColor, RoundedCornerShape(3.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = cert.certType.replace("_", " "),
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = plaqueOutlineColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = cert.entityType,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = PlatinumText.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cert.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = cert.artist,
                    fontSize = 11.sp,
                    color = PlatinumText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Earned $formattedDate",
                        fontSize = 9.sp,
                        color = PlatinumText.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${cert.plays} PLAYS",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        color = plaqueOutlineColor
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCertState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.WorkspacePremium,
            contentDescription = null,
            tint = PlatinumText.copy(alpha = 0.3f),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No milestones matching filters",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Keep scrobbling songs and records. Milestone records appear here once they reach specified play volumes (Gold = 50+, Platinum = 100+, Diamond = 200+).",
            fontSize = 10.sp,
            color = PlatinumText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp).padding(top = 4.dp)
        )
    }
}
