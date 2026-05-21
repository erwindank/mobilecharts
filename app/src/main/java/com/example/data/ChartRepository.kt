package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChartRepository(private val database: AppDatabase) {

    val userProfile: Flow<UserProfile?> = database.userProfileDao().getUserProfile()
    val recentScrobbles: Flow<List<Scrobble>> = database.scrobbleDao().getRecentScrobbles()
    val certifications: Flow<List<Certification>> = database.certificationDao().getAllCertifications()

    fun getCharts(chartType: String, timeframe: String, entityType: String): Flow<List<ChartEntry>> {
        return database.chartEntryDao().getCharts(chartType, timeframe, entityType)
    }

    fun getTimeframes(chartType: String): Flow<List<String>> {
        return database.chartEntryDao().getTimeframes(chartType)
    }

    suspend fun searchScrobbles(query: String): List<Scrobble> {
        return database.scrobbleDao().searchScrobbles(query).firstOrNull() ?: emptyList()
    }

    suspend fun addScrobble(scrobble: Scrobble) {
        database.scrobbleDao().insertScrobble(scrobble)
        
        // Update total plays on profile
        val currentProfile = database.userProfileDao().getUserProfile().firstOrNull() ?: UserProfile()
        val updatedProfile = currentProfile.copy(
            totalPlays = currentProfile.totalPlays + scrobble.playCount
        )
        database.userProfileDao().insertOrUpdateUserProfile(updatedProfile)
    }

    suspend fun updateScrobble(scrobble: Scrobble) {
        database.scrobbleDao().updateScrobble(scrobble)
    }

    suspend fun deleteScrobble(id: Int) {
        database.scrobbleDao().deleteScrobble(id)
    }

    suspend fun syncNow(newUsername: String? = null) {
        // Increment days/streak/plays for UI simulation
        val currentProfile = database.userProfileDao().getUserProfile().firstOrNull() ?: UserProfile()
        val updatedProfile = currentProfile.copy(
            username = newUsername ?: currentProfile.username,
            totalPlays = currentProfile.totalPlays + 42,
            dayStreak = currentProfile.dayStreak + 1
        )
        database.userProfileDao().insertOrUpdateUserProfile(updatedProfile)
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        database.userProfileDao().insertOrUpdateUserProfile(profile)
    }

    suspend fun loginUser(username: String) {
        val newProfile = UserProfile(
            username = username,
            joinedDate = "May 21, 2026",
            totalPlays = 1450,
            daysListened = 24,
            playsPerDay = 60,
            topArtist = "Lady Gaga",
            dayStreak = 3,
            pbStreak = 10
        )
        database.userProfileDao().insertOrUpdateUserProfile(newProfile)
    }

    suspend fun registerUser(username: String) {
        loginUser(username)
    }

    suspend fun preloadDataIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val existingProfile = database.userProfileDao().getUserProfile().firstOrNull()
            if (existingProfile == null) {
            // Populate database with rich sample data strictly based on the user's screenshots!
            val mainProfile = UserProfile()
            database.userProfileDao().insertOrUpdateUserProfile(mainProfile)

            // Scrobble preloads
            val now = System.currentTimeMillis()
            val hour = 3600 * 1000L
            val initialScrobbles = listOf(
                Scrobble(title = "I'M HIS, HE'S MINE", artist = "Katy Perry, Doechii", album = "143", timestamp = now - 2 * hour, playCount = 20),
                Scrobble(title = "Die With A Smile (Apple Music Live)", artist = "Lady Gaga, Bruno Mars", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 5 * hour, playCount = 20),
                Scrobble(title = "Need Your Love", artist = "OneRepublic", album = "Need Your Love", timestamp = now - 10 * hour, playCount = 11),
                Scrobble(title = "Memories of You", artist = "Slayyyter", album = "STARFUCKER", timestamp = now - 20 * hour, playCount = 10),
                Scrobble(title = "My Body", artist = "Slayyyter", album = "STARFUCKER", timestamp = now - 25 * hour, playCount = 10),
                Scrobble(title = "DAI DAI", artist = "Shakira, Burna Boy", album = "Dai Dai", timestamp = now - 27 * hour, playCount = 10),
                Scrobble(title = "Love Is Kind", artist = "The Chainsmokers, Oaks", album = "Love Is Kind", timestamp = now - 35 * hour, playCount = 9),
                Scrobble(title = "Survive", artist = "Lewis Capaldi", album = "Survive - EP", timestamp = now - 40 * hour, playCount = 9),
                Scrobble(title = "The One That Got Away", artist = "Katy Perry", album = "Teenage Dream", timestamp = now - 55 * hour, playCount = 9),
                Scrobble(title = "Abracadabra (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 62 * hour, playCount = 9),
                Scrobble(title = "Garden Of Eden (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 68 * hour, playCount = 9),
                Scrobble(title = "Perfect Celebrity (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 72 * hour, playCount = 9),
                Scrobble(title = "Vanish Into You (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 80 * hour, playCount = 9),
                Scrobble(title = "Disease (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 85 * hour, playCount = 8),
                Scrobble(title = "ZombieBoy (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 92 * hour, playCount = 8),
                Scrobble(title = "LoveDrug (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 100 * hour, playCount = 8),
                Scrobble(title = "The Beast (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 110 * hour, playCount = 8),
                Scrobble(title = "Killah (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem", timestamp = now - 120 * hour, playCount = 7),
                Scrobble(title = "Beat Up Chanel\$", artist = "Slayyyter", album = "STAYYTER - EP", timestamp = now - 130 * hour, playCount = 33),
                Scrobble(title = "Hit the Wall", artist = "Gracie Abrams", album = "Hit the Wall - Single", timestamp = now - 140 * hour, playCount = 14)
            )
            database.scrobbleDao().insertScrobbles(initialScrobbles)

            // Populate historical charts strictly based on the images!
            val timeframeWeekly1 = "17 May 2026 - 23 May 2026"
            val timeframeWeekly2 = "10 May 2026 - 16 May 2026"
            
            val weeklySongs = listOf(
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 1,
                    title = "Die With A Smile (Apple Music Live)", artist = "Lady Gaga, Bruno Mars", album = "Apple Music Live: MAYHEM Requiem",
                    plays = 20, peakRank = 1, weeksOnChart = 1, changeIndicator = "NEW"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 2,
                    title = "Feel", artist = "MOTHERMARY, HERO", album = "Feel",
                    plays = 12, peakRank = 2, weeksOnChart = 2, changeIndicator = "^3"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 3,
                    title = "Need Your Love", artist = "OneRepublic", album = "Need Your Love",
                    plays = 11, peakRank = 1, weeksOnChart = 4, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 4,
                    title = "Memories of You", artist = "Slayyyter", album = "STARFUCKER",
                    plays = 10, peakRank = 1, weeksOnChart = 4, changeIndicator = "v2"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 5,
                    title = "My Body", artist = "Slayyyter", album = "STARFUCKER",
                    plays = 10, peakRank = 2, weeksOnChart = 3, changeIndicator = "v2"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 6,
                    title = "DAI DAI", artist = "Shakira, Burna Boy", album = "Dai Dai",
                    plays = 10, peakRank = 6, weeksOnChart = 1, changeIndicator = "NEW"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 7,
                    title = "Love Is Kind", artist = "The Chainsmokers, Oaks", album = "Love Is Kind",
                    plays = 9, peakRank = 4, weeksOnChart = 2, changeIndicator = "v3"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 8,
                    title = "Survive", artist = "Lewis Capaldi", album = "Survive - EP",
                    plays = 9, peakRank = 8, weeksOnChart = 3, changeIndicator = "="
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 9,
                    title = "The One That Got Away", artist = "Katy Perry", album = "Teenage Dream",
                    plays = 9, peakRank = 4, weeksOnChart = 3, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "SONG", rank = 10,
                    title = "Abracadabra (Apple Music Live)", artist = "Lady Gaga", album = "Apple Music Live: MAYHEM Requiem",
                    plays = 9, peakRank = 10, weeksOnChart = 1, changeIndicator = "NEW"
                )
            )

            val weeklyArtists = listOf(
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 1,
                    title = "Lady Gaga", plays = 256, peakRank = 1, weeksOnChart = 114, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 2,
                    title = "Slayyyter", plays = 69, peakRank = 1, weeksOnChart = 9, changeIndicator = "v1"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 3,
                    title = "The Chainsmokers", plays = 44, peakRank = 1, weeksOnChart = 45, changeIndicator = "^6"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 4,
                    title = "Lewis Capaldi", plays = 43, peakRank = 2, weeksOnChart = 21, changeIndicator = "="
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 5,
                    title = "John Summit", plays = 37, peakRank = 1, weeksOnChart = 8, changeIndicator = "="
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 6,
                    title = "Oaks", plays = 30, peakRank = 6, weeksOnChart = 1, changeIndicator = "NEW"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 7,
                    title = "Demi Lovato", plays = 28, peakRank = 1, weeksOnChart = 27, changeIndicator = "v4"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 8,
                    title = "Bruno Mars", plays = 24, peakRank = 3, weeksOnChart = 4, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 9,
                    title = "Shakira", plays = 23, peakRank = 1, weeksOnChart = 56, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ARTIST", rank = 10,
                    title = "OneRepublic", plays = 23, peakRank = 1, weeksOnChart = 57, changeIndicator = "RE"
                )
            )

            val weeklyAlbums = listOf(
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 1,
                    title = "Apple Music Live: MAYHEM Requiem", artist = "Lady Gaga", plays = 168, peakRank = 1, weeksOnChart = 1, changeIndicator = "NEW"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 2,
                    title = "MAYHEM", artist = "Lady Gaga", plays = 46, peakRank = 1, weeksOnChart = 17, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 3,
                    title = "CTRL ESCAPE", artist = "John Summit", plays = 37, peakRank = 1, weeksOnChart = 6, changeIndicator = "="
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 4,
                    title = "WOR\$T GIRL IN AMERICA", artist = "Slayyyter", plays = 33, peakRank = 1, weeksOnChart = 9, changeIndicator = "v1"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 5,
                    title = "Love Is Kind", artist = "The Chainsmokers", plays = 30, peakRank = 5, weeksOnChart = 2, changeIndicator = "^5"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 6,
                    title = "STARFUCKER", artist = "Slayyyter", plays = 29, peakRank = 1, weeksOnChart = 4, changeIndicator = "v4"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 7,
                    title = "Survive - EP", artist = "Lewis Capaldi", plays = 23, peakRank = 7, weeksOnChart = 4, changeIndicator = "="
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 8,
                    title = "It's Not That Deep (Unless You Want It To Be)", artist = "Demi Lovato", plays = 16, peakRank = 3, weeksOnChart = 5, changeIndicator = "v4"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 9,
                    title = "Teenage Dream", artist = "Katy Perry", plays = 15, peakRank = 2, weeksOnChart = 34, changeIndicator = "RE"
                ),
                ChartEntry(
                    chartType = "WEEKLY", timeframe = timeframeWeekly1, entityType = "ALBUM", rank = 10,
                    title = "Born This Way", artist = "Lady Gaga", plays = 15, peakRank = 10, weeksOnChart = 1, changeIndicator = "NEW"
                )
            )

            // Populate monthly charts
            val monthlySongs = listOf(
                ChartEntry(chartType = "MONTHLY", timeframe = "May 2026", entityType = "SONG", rank = 1, title = "Beat Up Chanel$", artist = "Slayyyter", plays = 145, peakRank = 1, weeksOnChart = 4, changeIndicator = "="),
                ChartEntry(chartType = "MONTHLY", timeframe = "May 2026", entityType = "SONG", rank = 2, title = "Die With A Smile", artist = "Lady Gaga, Bruno Mars", plays = 120, peakRank = 2, weeksOnChart = 2, changeIndicator = "^10"),
                ChartEntry(chartType = "MONTHLY", timeframe = "May 2026", entityType = "SONG", rank = 3, title = "Memories of You", artist = "Slayyyter", plays = 112, peakRank = 1, weeksOnChart = 12, changeIndicator = "v1")
            )

            // Populate yearly charts
            val yearlySongs = listOf(
                ChartEntry(chartType = "YEARLY", timeframe = "2026", entityType = "SONG", rank = 1, title = "Memories of You", artist = "Slayyyter", plays = 450, peakRank = 1, weeksOnChart = 20, changeIndicator = "="),
                ChartEntry(chartType = "YEARLY", timeframe = "2026", entityType = "SONG", rank = 2, title = "Need Your Love", artist = "OneRepublic", plays = 380, peakRank = 2, weeksOnChart = 18, changeIndicator = "="),
                ChartEntry(chartType = "YEARLY", timeframe = "2026", entityType = "SONG", rank = 3, title = "Survive", artist = "Lewis Capaldi", plays = 320, peakRank = 3, weeksOnChart = 15, changeIndicator = "^5")
            )

            // Populate all-time charts
            val allTimeSongs = listOf(
                ChartEntry(chartType = "ALL_TIME", timeframe = "All-Time", entityType = "SONG", rank = 1, title = "The One That Got Away", artist = "Katy Perry", plays = 680, peakRank = 1, weeksOnChart = 154, changeIndicator = "="),
                ChartEntry(chartType = "ALL_TIME", timeframe = "All-Time", entityType = "SONG", rank = 2, title = "Die With A Smile", artist = "Lady Gaga, Bruno Mars", plays = 520, peakRank = 1, weeksOnChart = 45, changeIndicator = "="),
                ChartEntry(chartType = "ALL_TIME", timeframe = "All-Time", entityType = "SONG", rank = 3, title = "Memories of You", artist = "Slayyyter", plays = 480, peakRank = 1, weeksOnChart = 52, changeIndicator = "=")
            )

            database.chartEntryDao().insertChartEntries(weeklySongs)
            database.chartEntryDao().insertChartEntries(weeklyArtists)
            database.chartEntryDao().insertChartEntries(weeklyAlbums)
            database.chartEntryDao().insertChartEntries(monthlySongs)
            database.chartEntryDao().insertChartEntries(yearlySongs)
            database.chartEntryDao().insertChartEntries(allTimeSongs)

            // Populate certification wall
            val sampleCerts = listOf(
                Certification(title = "Die With A Smile (Apple Music Live)", artist = "Lady Gaga, Bruno Mars", certType = "GOLD", entityType = "SONG", plays = 52, timestamp = now),
                Certification(title = "Need Your Love", artist = "OneRepublic", certType = "PLATINUM", entityType = "SONG", plays = 114, timestamp = now - 5 * day),
                Certification(title = "Memories of You", artist = "Slayyyter", certType = "GOLD", entityType = "SONG", plays = 80, timestamp = now - 10 * day),
                Certification(title = "My Body", artist = "Slayyyter", certType = "GOLD", entityType = "SONG", plays = 65, timestamp = now - 12 * day),
                Certification(title = "Survive", artist = "Lewis Capaldi", certType = "GOLD", entityType = "SONG", plays = 79, timestamp = now - 15 * day),
                Certification(title = "The One That Got Away", artist = "Katy Perry", certType = "DIAMOND", entityType = "SONG", plays = 245, timestamp = now - 40 * day),
                Certification(title = "Apple Music Live: MAYHEM Requiem", artist = "Lady Gaga", certType = "GOLD", entityType = "ALBUM", plays = 168, timestamp = now),
                Certification(title = "STARFUCKER", artist = "Slayyyter", certType = "GOLD", entityType = "ALBUM", plays = 142, timestamp = now - 8 * day),
                Certification(title = "Teenage Dream", artist = "Katy Perry", certType = "PLATINUM", entityType = "ALBUM", plays = 310, timestamp = now - 30 * day),
                Certification(title = "MAYHEM", artist = "Lady Gaga", certType = "TRIPLE_DIAMOND", entityType = "ALBUM", plays = 1845, timestamp = now - 100 * day)
            )
            database.certificationDao().insertCertifications(sampleCerts)
        }
        } catch (e: Exception) {
            Log.e("ChartRepository", "Failed preloading sample data in database", e)
        }
    }

    companion object {
        private const val day = 24 * 3600 * 1000L
    }
}
