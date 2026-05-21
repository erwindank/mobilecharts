package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfile)
}

@Dao
interface ScrobbleDao {
    @Query("SELECT * FROM scrobbles ORDER BY timestamp DESC")
    fun getRecentScrobbles(): Flow<List<Scrobble>>

    @Query("SELECT * FROM scrobbles WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchScrobbles(query: String): Flow<List<Scrobble>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrobbles(scrobbles: List<Scrobble>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrobble(scrobble: Scrobble)

    @Update
    suspend fun updateScrobble(scrobble: Scrobble)

    @Query("DELETE FROM scrobbles WHERE id = :id")
    suspend fun deleteScrobble(id: Int)

    @Query("DELETE FROM scrobbles")
    suspend fun deleteAllScrobbles()
}

@Dao
interface ChartEntryDao {
    @Query("SELECT * FROM chart_entries WHERE chartType = :chartType AND timeframe = :timeframe AND entityType = :entityType ORDER BY rank ASC")
    fun getCharts(chartType: String, timeframe: String, entityType: String): Flow<List<ChartEntry>>

    @Query("SELECT DISTINCT timeframe FROM chart_entries WHERE chartType = :chartType ORDER BY id DESC")
    fun getTimeframes(chartType: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChartEntries(entries: List<ChartEntry>)

    @Query("DELETE FROM chart_entries WHERE chartType = :chartType AND timeframe = :timeframe AND entityType = :entityType")
    suspend fun clearCharts(chartType: String, timeframe: String, entityType: String)
}

@Dao
interface CertificationDao {
    @Query("SELECT * FROM certifications ORDER BY plays DESC")
    fun getAllCertifications(): Flow<List<Certification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertifications(certs: List<Certification>)

    @Query("DELETE FROM certifications")
    suspend fun clearAllCertifications()
}

@Database(
    entities = [UserProfile::class, Scrobble::class, ChartEntry::class, Certification::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun scrobbleDao(): ScrobbleDao
    abstract fun chartEntryDao(): ChartEntryDao
    abstract fun certificationDao(): CertificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dankcharts_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
