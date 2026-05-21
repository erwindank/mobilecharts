package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UserProfile
import com.example.ui.ChartViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: ChartViewModel,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues = PaddingValues(0.dp),
    onDismissOrComplete: () -> Unit = {}
) {
    val profile by viewModel.userProfile.collectAsState()
    val googleEmail by viewModel.googleUserEmail.collectAsState()
    val scope = rememberCoroutineScope()
    var showSheetSelectionDialog by remember { mutableStateOf(false) }

    // local mutable states for settings corresponding to the exact fields
    var displayName by remember { mutableStateOf("") }
    var timeZone by remember { mutableStateOf("America/Guatemala (GMT-6)") }
    var dataSourceType by remember { mutableStateOf("GOOGLE_SHEETS") }
    var sheetUrl by remember { mutableStateOf("") }
    var sheetTabName by remember { mutableStateOf("") }
    var lastfmUsername by remember { mutableStateOf("") }
    
    var certAlbumGold by remember { mutableStateOf("120") }
    var certAlbumPlatinum by remember { mutableStateOf("300") }
    var certAlbumDiamond by remember { mutableStateOf("600") }
    var certSongGold by remember { mutableStateOf("50") }
    var certSongPlatinum by remember { mutableStateOf("100") }
    var certSongDiamond by remember { mutableStateOf("200") }

    var eventsArtistCount by remember { mutableStateOf("200") }
    var keepCommaSeparated by remember { mutableStateOf(false) }
    var isSigningInGoogle by remember { mutableStateOf(false) }

    // Init inputs when profile loads
    LaunchedEffect(profile) {
        profile?.let { prof ->
            displayName = prof.username
            timeZone = prof.timeZone
            dataSourceType = prof.dataSourceType
            sheetUrl = prof.sheetUrl
            sheetTabName = prof.sheetTabName
            lastfmUsername = prof.lastfmUsername
            certAlbumGold = prof.certAlbumGold.toString()
            certAlbumPlatinum = prof.certAlbumPlatinum.toString()
            certAlbumDiamond = prof.certAlbumDiamond.toString()
            certSongGold = prof.certSongGold.toString()
            certSongPlatinum = prof.certSongPlatinum.toString()
            certSongDiamond = prof.certSongDiamond.toString()
            eventsArtistCount = prof.eventsArtistCount.toString()
            keepCommaSeparated = prof.keepCommaSeparatedName
        }
    }

    val activeProfile = profile ?: UserProfile()

    // Screen determines setup state or config view
    if (!activeProfile.onboardingCompleted) {
        // Render Onboarding connection screen
        OnboardingSetupView(
            viewModel = viewModel,
            isSigningInGoogle = isSigningInGoogle,
            googleEmail = googleEmail,
            onSourceSelected = { srcType ->
                dataSourceType = srcType
                // Instantly complete or update profile on first interaction
                scope.launch {
                    val updated = activeProfile.copy(
                        dataSourceType = srcType,
                        onboardingCompleted = false // Shows detailed settings next
                    )
                    viewModel.saveUserProfile(updated)
                    // Set detailed setup
                    showSheetSelectionDialog = true
                }
            },
            onSignInGoogleClick = {
                scope.launch {
                    isSigningInGoogle = true
                    delay(1500)
                    viewModel.signInWithGoogle()
                    isSigningInGoogle = false
                    // Prepopulate with sync template and immediately complete onboarding
                    val updated = activeProfile.copy(
                        googleAccountEmail = "erwindank@gmail.com",
                        username = "Erwin",
                        dataSourceType = "GOOGLE_SHEETS",
                        sheetUrl = "1ydtkm3-P_37m1Opim0IS5WfIs2LS1VRx_D8fOL4kFVM",
                        sheetTabName = "Full Raw Listening History",
                        timeZone = "America/Guatemala (GMT-6)",
                        onboardingCompleted = true
                    )
                    viewModel.saveUserProfile(updated)
                }
            },
            onContinueWithDefaults = {
                scope.launch {
                    viewModel.saveUserProfile(activeProfile.copy(onboardingCompleted = true))
                    onDismissOrComplete()
                }
            }
        )
    } else {
        // Render full settings page
        SettingsEditorView(
            displayName = displayName,
            onDisplayNameChange = { displayName = it },
            timeZone = timeZone,
            onTimeZoneChange = { timeZone = it },
            dataSourceType = dataSourceType,
            onSourceTypeChange = { dataSourceType = it },
            sheetUrl = sheetUrl,
            onSheetUrlChange = { sheetUrl = it },
            sheetTabName = sheetTabName,
            onSheetTabNameChange = { sheetTabName = it },
            lastfmUsername = lastfmUsername,
            onLastfmUsernameChange = { lastfmUsername = it },
            certAlbumGold = certAlbumGold,
            onCertAlbumGoldChange = { certAlbumGold = it },
            certAlbumPlatinum = certAlbumPlatinum,
            onCertAlbumPlatinumChange = { certAlbumPlatinum = it },
            certAlbumDiamond = certAlbumDiamond,
            onCertAlbumDiamondChange = { certAlbumDiamond = it },
            certSongGold = certSongGold,
            onCertSongGoldChange = { certSongGold = it },
            certSongPlatinum = certSongPlatinum,
            onCertSongPlatinumChange = { certSongPlatinum = it },
            certSongDiamond = certSongDiamond,
            onCertSongDiamondChange = { certSongDiamond = it },
            eventsArtistCount = eventsArtistCount,
            onEventsArtistCountChange = { eventsArtistCount = it },
            keepCommaSeparated = keepCommaSeparated,
            onKeepCommaSeparatedChange = { keepCommaSeparated = it },
            googleEmail = googleEmail,
            onGoogleSignIn = {
                scope.launch {
                    isSigningInGoogle = true
                    delay(1000)
                    viewModel.signInWithGoogle()
                    isSigningInGoogle = false
                }
            },
            onGoogleSignOut = {
                viewModel.signOutGoogle()
            },
            onCancel = {
                onDismissOrComplete()
            },
            onSave = {
                scope.launch {
                    val updated = activeProfile.copy(
                        username = displayName.ifBlank { "Erwin" },
                        timeZone = timeZone,
                        dataSourceType = dataSourceType,
                        sheetUrl = sheetUrl,
                        sheetTabName = sheetTabName,
                        lastfmUsername = lastfmUsername,
                        certAlbumGold = certAlbumGold.toIntOrNull() ?: 120,
                        certAlbumPlatinum = certAlbumPlatinum.toIntOrNull() ?: 300,
                        certAlbumDiamond = certAlbumDiamond.toIntOrNull() ?: 600,
                        certSongGold = certSongGold.toIntOrNull() ?: 50,
                        certSongPlatinum = certSongPlatinum.toIntOrNull() ?: 100,
                        certSongDiamond = certSongDiamond.toIntOrNull() ?: 200,
                        eventsArtistCount = eventsArtistCount.toIntOrNull() ?: 200,
                        keepCommaSeparatedName = keepCommaSeparated,
                        onboardingCompleted = true
                    )
                    viewModel.saveUserProfile(updated)
                    delay(300)
                    onDismissOrComplete()
                }
            }
        )
    }

    // Config setup sheet overlay
    if (showSheetSelectionDialog) {
        Dialog(onDismissRequest = { showSheetSelectionDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, CustomOutline, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CONFIGURE_DATA_SOURCE",
                        style = MaterialTheme.typography.titleMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Customize sheet options, thresholds, event calendars, and formatting on the settings dashboard.",
                        fontSize = 11.sp,
                        color = PlatinumText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                // Save with onboarding Completed to open immediate View Editor
                                viewModel.saveUserProfile(activeProfile.copy(onboardingCompleted = true))
                                showSheetSelectionDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("OPEN_SETTINGS_DASHBOARD", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingSetupView(
    viewModel: ChartViewModel,
    isSigningInGoogle: Boolean,
    googleEmail: String?,
    onSourceSelected: (String) -> Unit,
    onSignInGoogleClick: () -> Unit,
    onContinueWithDefaults: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.95f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(30.dp))
                // Logo matching dankcharts.fm
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "dankcharts",
                        fontSize = 42.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = ".fm",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your listening history. Your charts. Your impeccable taste.",
                    fontSize = 13.sp,
                    color = PlatinumText,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center
                )
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                if (isSigningInGoogle) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NeonCyan)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Contacting Google cloud accounts...", fontSize = 11.sp, color = Color.White)
                        }
                    }
                } else if (googleEmail != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF39D353), RoundedCornerShape(8.dp))
                            .background(Color(0xFF39D353).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF39D353), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synced setup: $googleEmail", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Text(
                                "Disconnect",
                                color = Color(0xFFF13C4A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { viewModel.signOutGoogle() }
                            )
                        }
                    }
                } else {
                    // Google Sign-In Restorer Button matching Screenshot 2
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, CustomOutline, RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { onSignInGoogleClick() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Custom clean Google mini G styled vector representation
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DeepNavyBg
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Sign in with Google to restore your settings",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Your Google account only remembers your setup — it has no access to your music data.",
                    fontSize = 10.sp,
                    color = PlatinumText.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Connection selection
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "HOW WOULD YOU LIKE TO IMPORT YOUR DATA?",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Option card: Google sheets
            item {
                OnboardingSourceCard(
                    title = "Google Sheets",
                    description = "Your full history, your way — no limits, no API restrictions",
                    icon = Icons.Default.GridOn,
                    badgeColor = Color(0xFF107C41),
                    onClick = { onSourceSelected("GOOGLE_SHEETS") }
                )
            }

            // Option card: Lastfm
            item {
                OnboardingSourceCard(
                    title = "Last.fm",
                    description = "Load your full scrobble history directly from Last.fm",
                    icon = Icons.Default.MusicVideo,
                    badgeColor = Color(0xFFD51007),
                    onClick = { onSourceSelected("LASTFM") }
                )
            }

            // Option card: Upload file
            item {
                OnboardingSourceCard(
                    title = "Upload File",
                    description = "Import from CSV, Last.fm export, Spotify ZIP, or Deezer XLSX",
                    icon = Icons.Default.UploadFile,
                    badgeColor = Color(0xFFF1C40F),
                    onClick = { onSourceSelected("UPLOAD_FILE") }
                )
            }

            // Already setup link matching Screenshot 2
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.clickable { onContinueWithDefaults() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already set up? ",
                        fontSize = 12.sp,
                        color = PlatinumText
                    )
                    Text(
                        text = "Continue to charts",
                        fontSize = 12.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Divider(color = CustomOutline, thickness = 1.dp)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Questions or need help? Contact Support • Terms & Privacy",
                    fontSize = 10.sp,
                    color = PlatinumText.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun OnboardingSourceCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CustomOutline, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = badgeColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = description, fontSize = 10.sp, color = PlatinumText, lineHeight = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsEditorView(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    timeZone: String,
    onTimeZoneChange: (String) -> Unit,
    dataSourceType: String,
    onSourceTypeChange: (String) -> Unit,
    sheetUrl: String,
    onSheetUrlChange: (String) -> Unit,
    sheetTabName: String,
    onSheetTabNameChange: (String) -> Unit,
    lastfmUsername: String,
    onLastfmUsernameChange: (String) -> Unit,
    certAlbumGold: String,
    onCertAlbumGoldChange: (String) -> Unit,
    certAlbumPlatinum: String,
    onCertAlbumPlatinumChange: (String) -> Unit,
    certAlbumDiamond: String,
    onCertAlbumDiamondChange: (String) -> Unit,
    certSongGold: String,
    onCertSongGoldChange: (String) -> Unit,
    certSongPlatinum: String,
    onCertSongPlatinumChange: (String) -> Unit,
    certSongDiamond: String,
    onCertSongDiamondChange: (String) -> Unit,
    eventsArtistCount: String,
    onEventsArtistCountChange: (String) -> Unit,
    keepCommaSeparated: Boolean,
    onKeepCommaSeparatedChange: (Boolean) -> Unit,
    googleEmail: String?,
    onGoogleSignIn: () -> Unit,
    onGoogleSignOut: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    var showTimeZonesDropdown by remember { mutableStateOf(false) }
    var showArtistDropdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepNavyBg),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // DATA SOURCE SECTION (Screenshot 1)
        item {
            Text(
                text = "DATA SOURCE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Display Name field
            Text("DISPLAY NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CustomOutline,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                "Shown in the masthead — e.g. \"${displayName.ifBlank { "Erwin" }}'s Personal Music Charts\"",
                fontSize = 10.sp,
                color = PlatinumText.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Time Zone dropdown
            Text("TIME ZONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CustomOutline, RoundedCornerShape(4.dp))
                    .clickable { showTimeZonesDropdown = !showTimeZonesDropdown }
                    .padding(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(timeZone, fontSize = 12.sp, color = Color.White)
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan)
                }
                DropdownMenu(
                    expanded = showTimeZonesDropdown,
                    onDismissRequest = { showTimeZonesDropdown = false },
                    modifier = Modifier.background(DarkSlateSurface)
                ) {
                    listOf(
                        "America/Guatemala (GMT-6)",
                        "America/New_York (GMT-5)",
                        "Europe/London (GMT+0)",
                        "Europe/Paris (GMT+1)",
                        "Asia/Tokyo (GMT+9)"
                    ).forEach { tz ->
                        DropdownMenuItem(
                            text = { Text(tz, color = Color.White, fontSize = 12.sp) },
                            onClick = {
                                onTimeZoneChange(tz)
                                showTimeZonesDropdown = false
                            }
                        )
                    }
                }
            }
            Text(
                "Chart periods (weeks, months, years) begin and end at midnight in this timezone",
                fontSize = 10.sp,
                color = PlatinumText.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
        }

        // Radio Source Selectors
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("GOOGLE_SHEETS" to "Google Sheets", "LASTFM" to "Last.fm", "UPLOAD_FILE" to "Upload File").forEach { (typeKey, label) ->
                    val isSelected = dataSourceType == typeKey
                    Row(
                        modifier = Modifier
                            .selectable(
                                selected = isSelected,
                                onClick = { onSourceTypeChange(typeKey) }
                            )
                            .padding(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSourceTypeChange(typeKey) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = NeonCyan,
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Conditional options based on select
        item {
            when (dataSourceType) {
                "GOOGLE_SHEETS" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("No tracking sheet yet? ", fontSize = 10.sp, color = PlatinumText)
                            Text("Copy our ready-to-use template →", fontSize = 10.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(imageVector = Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(12.dp), tint = NeonCyan)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Setup guide", fontSize = 10.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("SHEET URL OR ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText)
                        OutlinedTextField(
                            value = sheetUrl,
                            onValueChange = onSheetUrlChange,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CustomOutline,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            placeholder = { Text("Enter Google Sheet ID or URL", color = Color.Gray, fontSize = 11.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontFamily = FontFamily.Monospace),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Text("TAB NAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText)
                        OutlinedTextField(
                            value = sheetTabName,
                            onValueChange = onSheetTabNameChange,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CustomOutline,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            placeholder = { Text("Full Raw Listening History", color = Color.Gray, fontSize = 11.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text(
                            "The sheet must be shared as \"Anyone with the link can view\"",
                            fontSize = 10.sp,
                            color = PlatinumText.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(10.dp), tint = PlatinumText)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Enable auto-sync & manual play entry (optional)", fontSize = 10.sp, color = PlatinumText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "LASTFM" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("LAST.FM USERNAME", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText)
                        OutlinedTextField(
                            value = lastfmUsername,
                            onValueChange = onLastfmUsernameChange,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = CustomOutline,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Text(
                            "We will pull your scrobble timeline feed directly from Last.fm API endpoints.",
                            fontSize = 10.sp,
                            color = PlatinumText.copy(alpha = 0.5f)
                        )
                    }
                }
                else -> { // UPLOAD_FILE
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CustomOutline, RoundedCornerShape(6.dp))
                            .background(DarkSlateSurface)
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Drag & drop files or click to upload CSV", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("CSV, Last.fm scrobble export, Spotify ZIP, or Deezer XLSX up to 50MB", fontSize = 9.sp, color = PlatinumText)
                    }
                }
            }
        }

        // CERTIFICATION THRESHOLDS SECTION
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "CERTIFICATION THRESHOLDS (PLAYS)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // ALBUMS column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Albums", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    
                    ThresholdInputRow(label = "Gold", icon = Icons.Default.Star, value = certAlbumGold, onValueChange = onCertAlbumGoldChange)
                    ThresholdInputRow(label = "Platinum", icon = Icons.Default.Radio, value = certAlbumPlatinum, onValueChange = onCertAlbumPlatinumChange)
                    ThresholdInputRow(badgeCharacter = "♦", label = "Diamond", value = certAlbumDiamond, onValueChange = onCertAlbumDiamondChange)
                }

                // SONGS column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Songs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)

                    ThresholdInputRow(label = "Gold", icon = Icons.Default.Star, value = certSongGold, onValueChange = onCertSongGoldChange)
                    ThresholdInputRow(label = "Platinum", icon = Icons.Default.Radio, value = certSongPlatinum, onValueChange = onCertSongPlatinumChange)
                    ThresholdInputRow(badgeCharacter = "♦", label = "Diamond", value = certSongDiamond, onValueChange = onCertSongDiamondChange)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Minimum plays to earn each badge. Changes apply on Save & Load.",
                    fontSize = 10.sp,
                    color = PlatinumText.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Reset to defaults",
                    fontSize = 10.sp,
                    color = AccentBlue,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onCertAlbumGoldChange("120")
                            onCertAlbumPlatinumChange("300")
                            onCertAlbumDiamondChange("600")
                            onCertSongGoldChange("50")
                            onCertSongPlatinumChange("100")
                            onCertSongDiamondChange("200")
                        }
                        .padding(start = 10.dp)
                )
            }
        }

        // EVENTS CALENDAR SECTION
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "EVENTS CALENDAR",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Show for top ", fontSize = 11.sp, color = PlatinumText)
                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .border(1.dp, CustomOutline, RoundedCornerShape(4.dp))
                        .clickable { showArtistDropdown = !showArtistDropdown }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(eventsArtistCount, fontSize = 11.sp, color = Color.White)
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                    }
                    DropdownMenu(
                        expanded = showArtistDropdown,
                        onDismissRequest = { showArtistDropdown = false },
                        modifier = Modifier.background(DarkSlateSurface)
                    ) {
                        listOf("100", "200", "500", "1000").forEach { count ->
                            DropdownMenuItem(
                                text = { Text(count, color = Color.White, fontSize = 11.sp) },
                                onClick = {
                                    onEventsArtistCountChange(count)
                                    showArtistDropdown = false
                                }
                            )
                        }
                    }
                }
                Text(" all-time artists.", fontSize = 11.sp, color = PlatinumText)
                Spacer(modifier = Modifier.weight(1f))
                Text("all-time artists", fontSize = 10.sp, color = PlatinumText.copy(alpha = 0.5f))
            }
            Text(
                "Number of top all-time artists to fetch birthdays & anniversaries for",
                fontSize = 10.sp,
                color = PlatinumText.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ARTIST NAMES SECTION
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "ARTIST NAMES",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(0.82f)) {
                    Text("Keep comma-separated names as one artist", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "\"Artist A, Artist B\" is counted as one artist instead of splitting into two. Useful if your data uses commas within a single artist name, or if you prefer Last.fm-style behavior where the full artist string is always kept intact.",
                        fontSize = 10.sp,
                        color = PlatinumText.copy(alpha = 0.5f),
                        lineHeight = 13.sp
                    )
                }
                Switch(
                    checked = keepCommaSeparated,
                    onCheckedChange = onKeepCommaSeparatedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepNavyBg,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f),
                        uncheckedBorderColor = CustomOutline
                    )
                )
            }
        }

        // GOOGLE ACCOUNT SYNC MANAGEMENT SLOT
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "GOOGLE ACCOUNT SYNC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (googleEmail != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSlateActive),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF39D353).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Backup, contentDescription = null, tint = Color(0xFF39D353), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Synced with Google", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(googleEmail, fontSize = 9.sp, color = PlatinumText)
                            }
                        }
                        Button(
                            onClick = onGoogleSignOut,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF13C4A)),
                            shape = RoundedCornerShape(4.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("Disconnect", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onGoogleSignIn,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CustomOutline, RoundedCornerShape(6.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Connect Google Account to sync setup online", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }

        // ACTIONS BLOCK: CANCEL AND SAVE & LOAD
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = CustomOutline, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(1.dp, CustomOutline, RoundedCornerShape(6.dp))
                ) {
                    Text("Cancel", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                ) {
                    Text("Save & Load", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ThresholdInputRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    badgeCharacter: String? = null,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            if (icon != null) {
                val tint = when (label) {
                    "Gold" -> GoldCert
                    "Platinum" -> PlatinumCert
                    else -> DiamondCert
                }
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = tint)
            } else if (badgeCharacter != null) {
                Text(
                    text = badgeCharacter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DiamondCert,
                    modifier = Modifier.width(14.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 11.sp, color = PlatinumText)
        }

        OutlinedTextField(
            value = value,
            onValueChange = { newValue: String -> if (newValue.all { char: Char -> char.isDigit() }) onValueChange(newValue) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CustomOutline,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 11.sp, textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(56.dp)
                .fillMaxHeight(),
            singleLine = true
        )
    }
}
