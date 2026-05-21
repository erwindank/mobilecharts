package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val username: String = "Erwin Solorzano",
    val joinedDate: String = "January 9, 2016",
    val totalPlays: Int = 192156,
    val daysListened: Int = 3097,
    val playsPerDay: Int = 62,
    val topArtist: String = "Taylor Swift",
    val dayStreak: Int = 52,
    val pbStreak: Int = 169,
    val bio: String = "Your listening history. Your charts. Your legacy.",
    
    // Custom import/sync settings
    val timeZone: String = "America/Guatemala (GMT-6)",
    val dataSourceType: String = "GOOGLE_SHEETS",
    val sheetUrl: String = "1ydtkm3-P_37m1Opim0IS5WfIs2LS1VRx_D8fOL4kFVM",
    val sheetTabName: String = "Full Raw Listening History",
    val lastfmUsername: String = "erwindank",
    val certAlbumGold: Int = 120,
    val certAlbumPlatinum: Int = 300,
    val certAlbumDiamond: Int = 600,
    val certSongGold: Int = 50,
    val certSongPlatinum: Int = 100,
    val certSongDiamond: Int = 200,
    val eventsArtistCount: Int = 200,
    val keepCommaSeparatedName: Boolean = false,
    val googleAccountEmail: String = "erwindank@gmail.com",
    val onboardingCompleted: Boolean = false
) : Serializable

@Entity(tableName = "scrobbles")
data class Scrobble(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val album: String,
    val timestamp: Long,
    val playCount: Int = 1
) : Serializable

@Entity(tableName = "chart_entries")
data class ChartEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chartType: String,       // WEEKLY, MONTHLY, YEARLY, ALL_TIME
    val timeframe: String,       // "17 May 2026 - 23 May 2026", "May 2026", "2026"
    val entityType: String,      // SONG, ARTIST, ALBUM
    val rank: Int,
    val title: String,
    val artist: String = "",
    val album: String = "",
    val plays: Int,
    val peakRank: Int,
    val weeksOnChart: Int,
    val changeIndicator: String,  // NEW, RE, UP (e.g. "^3"), DOWN (e.g. "v2"), SAME ("=")
    val imageUrl: String = ""
) : Serializable

@Entity(tableName = "certifications")
data class Certification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artist: String,
    val certType: String,        // GOLD, PLATINUM, DIAMOND, TRIPLE_DIAMOND
    val entityType: String,      // SONG, ALBUM
    val plays: Int,
    val timestamp: Long
) : Serializable
