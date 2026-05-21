package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Scrobble
import com.example.ui.ChartViewModel
import com.example.ui.theme.DarkSlateActive
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DeepNavyBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PlatinumText
import com.example.ui.theme.GlassBg
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.CustomOutline
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RawDataScreen(
    viewModel: ChartViewModel,
    innerPadding: PaddingValues
) {
    val scrobbles by viewModel.scrobbles.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editScrobbleItem by remember { mutableStateOf<Scrobble?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(innerPadding)
            .testTag("raw_data_screen")
    ) {
        // 1. TOP HEADER BAR WITH ADD OPTION
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "RAW SCROBBLE DATA",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "${scrobbles.size} plays loaded",
                    fontSize = 11.sp,
                    color = NeonCyan
                )
            }

            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(32.dp)
                    .testTag("add_scrobble_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, sizeModifier, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Play", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("scrobble_search"),
            placeholder = { Text("Search by title, artist, or album...", fontSize = 12.sp, color = PlatinumText.copy(alpha = 0.5f)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PlatinumText.copy(alpha = 0.5f)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear search", tint = PlatinumText)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CustomOutline,
                focusedContainerColor = DarkSlateSurface,
                unfocusedContainerColor = DarkSlateSurface,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // COLUMN HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSlateSurface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("TITLE - ARTIST", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.weight(1f))
            Text("PLAYDATE / ALBUM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(120.dp), textAlign = TextAlign.End)
            Text("ACTIONS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PlatinumText.copy(alpha = 0.5f), modifier = Modifier.width(80.dp), textAlign = TextAlign.Center)
        }

        // 3. SCROLLING LIST
        if (scrobbles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No scrobbles found matches criteria.",
                    fontSize = 12.sp,
                    color = PlatinumText,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(scrobbles) { item ->
                    ScrobbleRowItem(
                        scrobble = item,
                        onEdit = { editScrobbleItem = item },
                        onDelete = { viewModel.removeScrobble(item.id) }
                    )
                }
            }
        }
    }

    // Interactive Add Dialog Box
    if (showAddDialog) {
        ScrobbleFormDialog(
            title = "Manual Play Log",
            confirmLabel = "LOG SCROBBLE",
            onDismiss = { showAddDialog = false },
            onConfirm = { t, a, alb ->
                viewModel.addManualScrobble(t, a, alb)
                showAddDialog = false
            }
        )
    }

    // Interactive Edit Dialog Box
    editScrobbleItem?.let { item ->
        ScrobbleFormDialog(
            title = "Correct Scrobble Play",
            confirmLabel = "APPLY RULES",
            initialTitle = item.title,
            initialArtist = item.artist,
            initialAlbum = item.album,
            onDismiss = { editScrobbleItem = null },
            onConfirm = { t, a, alb ->
                viewModel.correctScrobble(item.id, t, a, alb)
                editScrobbleItem = null
            }
        )
    }
}

@Composable
fun ScrobbleRowItem(
    scrobble: Scrobble,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM HH:mm", Locale.US).format(Date(scrobble.timestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .testTag("scrobble_row_${scrobble.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Title + Artist
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scrobble.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = scrobble.artist,
                    fontSize = 10.sp,
                    color = PlatinumText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Middle Date + Album
            Column(modifier = Modifier.width(120.dp), horizontalAlignment = Alignment.End) {
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
                Text(
                    text = scrobble.album,
                    fontSize = 9.sp,
                    color = PlatinumText.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Action buttons
            Row(
                modifier = Modifier.width(80.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp).testTag("edit_scrobble_${scrobble.id}")) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit scrobble", tint = NeonCyan, modifier = Modifier.size(14.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).testTag("delete_scrobble_${scrobble.id}")) {
                    Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Delete scrobble", tint = Color(0xFFF13C4A), modifier = Modifier.size(16.dp))
                }
            }
        }
        Divider(color = CustomOutline, thickness = 1.dp)
    }
}

@Composable
fun ScrobbleFormDialog(
    title: String,
    confirmLabel: String,
    initialTitle: String = "",
    initialArtist: String = "",
    initialAlbum: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var songTitle by remember { mutableStateOf(initialTitle) }
    var artistName by remember { mutableStateOf(initialArtist) }
    var albumName by remember { mutableStateOf(initialAlbum) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = DarkSlateSurface,
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, CustomOutline, RoundedCornerShape(12.dp))
                .testTag("scrobble_form_dialog")
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Track fields
                OutlinedTextField(
                    value = songTitle,
                    onValueChange = { songTitle = it },
                    label = { Text("Song Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CustomOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_title_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = artistName,
                    onValueChange = { artistName = it },
                    label = { Text("Artist Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CustomOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_artist_field")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = { Text("Album Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = CustomOutline,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_album_field")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL", color = PlatinumText)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (songTitle.isNotBlank() && artistName.isNotBlank() && albumName.isNotBlank()) {
                                onConfirm(songTitle, artistName, albumName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(4.dp),
                        enabled = songTitle.isNotBlank() && artistName.isNotBlank() && albumName.isNotBlank(),
                        modifier = Modifier.testTag("dialog_submit_btn")
                    ) {
                        Text(confirmLabel, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private val sizeModifier = Modifier.size(14.dp)
