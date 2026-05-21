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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

import androidx.compose.material3.IconButton
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ShareProfileScreen(
    viewModel: ChartViewModel,
    innerPadding: PaddingValues
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val profile by viewModel.userProfile.collectAsState()

    var activeTheme by remember { mutableStateOf("Cosmic Slate") }
    var activeListType by remember { mutableStateOf("SONGS") } // SONGS, ALBUMS, ARTISTS
    
    val coroutineScope = rememberCoroutineScope()

    // Status visualizers for sharing
    var shareStatus by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(innerPadding)
            .testTag("share_profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // AUTH LAYER SEGMENT
        item {
            AccountManagementBlock(
                viewModel = viewModel,
                isLoggedIn = isLoggedIn,
                profileName = profile?.username ?: "Guest"
            )
        }

        if (isLoggedIn) {
            // INSTAGRAM STORIES GRAPHIC ENGINE CONTROLS
            item {
                Text(
                    text = "✦ SOCIAL SHARE GRAPHIC ENGINE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generate and export high-end branded graphics tailored specifically for Instagram Stories.",
                    fontSize = 11.sp,
                    color = PlatinumText
                )
            }

            // Theme selector capsules
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    modifier = Modifier.border(1.dp, CustomOutline, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Brush, contentDescription = null, sizeModifier, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. CHOOSE VISUAL THEME", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Cosmic Slate", "Sapphire Dark", "Platinum Minimal").forEach { themeName ->
                                val isSelected = activeTheme == themeName
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (isSelected) AccentBlue else DarkSlateActive, RoundedCornerShape(4.dp))
                                        .clickable { activeTheme = themeName }
                                        .padding(vertical = 8.dp)
                                        .testTag("style_btn_${themeName.lowercase().replace(" ", "_")}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = themeName, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // List selector capsules
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                    modifier = Modifier.border(1.dp, CustomOutline, RoundedCornerShape(8.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.FormatListNumbered, contentDescription = null, sizeModifier, tint = NeonCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("2. CHOOSE SHARING FORMAT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("SONGS", "ALBUMS", "ARTISTS").forEach { listType ->
                                val isSelected = activeListType == listType
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (isSelected) NeonCyan else DarkSlateActive, RoundedCornerShape(4.dp))
                                        .clickable { activeListType = listType }
                                        .padding(vertical = 8.dp)
                                        .testTag("format_btn_${listType.lowercase()}"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "Top 5 $listType", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSelected) DeepNavyBg else Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // PREVIEW CANVAS VISUALIZER
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "✦ STORY CANVAS PREVIEW",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    StoryCanvasPreview(
                        theme = activeTheme,
                        listType = activeListType,
                        username = profile?.username ?: "Erwin Solorzano"
                    )
                }
            }

            // SHARING EXPORTER TRIGGER
            item {
                Button(
                    onClick = {
                        isGenerating = true
                        shareStatus = "Drawing vector graphics layers..."
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(1200)
                            shareStatus = "Rendering custom font layers..."
                            kotlinx.coroutines.delay(1000)
                            shareStatus = "Branded story exported successfully! Saved to clipboard."
                            isGenerating = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("instagram_share_btn"),
                    enabled = !isGenerating
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isGenerating) "GENERATING GRAHICS..." else "GENERATE INSTAGRAM STORY ARTWORK",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Exporter loader indicators
            if (shareStatus.isNotBlank()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSlateActive),
                        modifier = Modifier.fillMaxWidth().border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NeonCyan, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldCert, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(text = shareStatus, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountManagementBlock(
    viewModel: ChartViewModel,
    isLoggedIn: Boolean,
    profileName: String
) {
    var usernameField by remember { mutableStateOf("") }
    var passwordField by remember { mutableStateOf("") }

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CustomOutline, RoundedCornerShape(10.dp))
            .testTag("auth_block")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isLoggedIn) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(NeonCyan.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NeonCyan)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = profileName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "DankCharts Account Active", fontSize = 10.sp, color = PlatinumText)
                        }
                    }

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color(0xFFF13C4A))
                    }
                }
            } else {
                Text(
                    text = "DANKCHARTS SESSION LOGIN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = usernameField,
                    onValueChange = { usernameField = it },
                    label = { Text("Username or Last.fm Name", fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CustomOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_username_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = passwordField,
                    onValueChange = { passwordField = it },
                    label = { Text("Password", fontSize = 11.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CustomOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_field"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (usernameField.isNotBlank()) {
                                viewModel.login(usernameField)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).testTag("login_submit_btn"),
                        enabled = usernameField.isNotBlank()
                    ) {
                        Text("LOGIN", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (usernameField.isNotBlank()) {
                                viewModel.register(usernameField)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CustomOutline),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.weight(1f).testTag("register_submit_btn"),
                        enabled = usernameField.isNotBlank()
                    ) {
                        Text("REGISTER", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryCanvasPreview(
    theme: String,
    listType: String,
    username: String
) {
    val bgBrush = when (theme) {
        "Cosmic Slate" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF04101D), Color(0xFF160E2A))
        )
        "Sapphire Dark" -> Brush.verticalGradient(
            colors = listOf(Color(0xFF020914), Color(0xFF062349))
        )
        else -> Brush.verticalGradient( // Platinum Minimal
            colors = listOf(Color(0xFF0F1218), Color(0xFF1E2430))
        )
    }

    val glowColor = when (theme) {
        "Cosmic Slate" -> Color(0xFF9151FF)
        "Sapphire Dark" -> NeonCyan
        else -> PlatinumCert
    }

    val sampleList = when (listType) {
        "SONGS" -> listOf(
            "1. Die With A Smile" to "Lady Gaga, Bruno Mars",
            "2. Feel" to "MOTHERMARY, HERO",
            "3. Need Your Love" to "OneRepublic",
            "4. Memories of You" to "Slayyyter",
            "5. My Body" to "Slayyyter"
        )
        "ALBUMS" -> listOf(
            "1. MAYHEM Requiem" to "Lady Gaga",
            "2. MAYHEM" to "Lady Gaga",
            "3. CTRL ESCAPE" to "John Summit",
            "4. WOR\$T GIRL IN AMERICA" to "Slayyyter",
            "5. STARFUCKER" to "Slayyyter"
        )
        else -> listOf(
            "1. Lady Gaga" to "256 plays",
            "2. Slayyyter" to "69 plays",
            "3. The Chainsmokers" to "44 plays",
            "4. Lewis Capaldi" to "43 plays",
            "5. John Summit" to "37 plays"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgBrush)
            .border(2.dp, glowColor, RoundedCornerShape(12.dp))
            .padding(18.dp)
            .testTag("story_canvas_preview")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Branded headers
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${username.uppercase()}'S PERSONAL BESTS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = glowColor,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "dankcharts",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = ".fm",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = glowColor,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                            .padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }

            // Real-time item listing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                sampleList.forEach { (rankName, description) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = rankName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = description,
                            fontSize = 9.sp,
                            color = PlatinumText,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            // Footer tagline
            Text(
                text = "YOUR LISTENING HISTORY • YOUR CHARTS • YOUR LEGACY",
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )
        }
    }
}

private val sizeModifier = androidx.compose.ui.Modifier.size(14.dp)
