package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChartEntry
import com.example.data.ChartRepository
import com.example.data.Scrobble
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChartViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = ChartRepository(database)

    // Reactive database states
    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val certifications = repository.certifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI selections & states
    private val _selectedChartType = MutableStateFlow("WEEKLY")
    val selectedChartType: StateFlow<String> = _selectedChartType.asStateFlow()

    private val _selectedTimeframe = MutableStateFlow("17 May 2026 - 23 May 2026")
    val selectedTimeframe: StateFlow<String> = _selectedTimeframe.asStateFlow()

    private val _selectedEntityType = MutableStateFlow("SONG")
    val selectedEntityType: StateFlow<String> = _selectedEntityType.asStateFlow()

    private val _selectedChartSize = MutableStateFlow(10)
    val selectedChartSize: StateFlow<Int> = _selectedChartSize.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Dynamically query available timeframes based on selected chart type
    val availableTimeframes: StateFlow<List<String>> = _selectedChartType
        .flatMapLatest { chartType ->
            repository.getTimeframes(chartType)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("17 May 2026 - 23 May 2026"))

    // Combine chart filter states to produce the reactive chart list
    val charts: StateFlow<List<ChartEntry>> = combine(
        _selectedChartType,
        _selectedTimeframe,
        _selectedEntityType,
        _selectedChartSize
    ) { type, timeframe, entity, size ->
        Triple(type, timeframe, entity) to size
    }.flatMapLatest { (filters, size) ->
        val (type, timeframe, entity) = filters
        val activeTimeframe = if (type == "ALL_TIME") "All-Time" else if (type == "MONTHLY" && timeframe.contains("May -")) "May 2026" else timeframe
        repository.getCharts(type, activeTimeframe, entity)
    }.combine(_selectedChartSize) { list, size ->
        list.take(size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive list of raw scrobbles filtered by query
    val scrobbles: StateFlow<List<Scrobble>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.recentScrobbles
            } else {
                database.scrobbleDao().searchScrobbles(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.preloadDataIfEmpty()
        }
    }

    // Actions
    fun setChartType(type: String) {
        _selectedChartType.value = type
        if (type == "ALL_TIME") {
            _selectedTimeframe.value = "All-Time"
        } else if (type == "MONTHLY") {
            _selectedTimeframe.value = "May 2026"
        } else if (type == "YEARLY") {
            _selectedTimeframe.value = "2026"
        } else {
            _selectedTimeframe.value = "17 May 2026 - 23 May 2026"
        }
    }

    fun setTimeframe(timeframe: String) {
        _selectedTimeframe.value = timeframe
    }

    fun setEntityType(type: String) {
        _selectedEntityType.value = type
    }

    fun setChartSize(size: Int) {
        _selectedChartSize.value = size
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncNow()
            _isSyncing.value = false
        }
    }

    fun addManualScrobble(title: String, artist: String, album: String) {
        viewModelScope.launch {
            repository.addScrobble(
                Scrobble(
                    title = title,
                    artist = artist,
                    album = album,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun correctScrobble(id: Int, title: String, artist: String, album: String) {
        viewModelScope.launch {
            repository.updateScrobble(
                Scrobble(
                    id = id,
                    title = title,
                    artist = artist,
                    album = album,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun removeScrobble(id: Int) {
        viewModelScope.launch {
            repository.deleteScrobble(id)
        }
    }

    private val _googleUserEmail = MutableStateFlow<String?>(null)
    val googleUserEmail: StateFlow<String?> = _googleUserEmail.asStateFlow()

    fun signInWithGoogle(email: String = "erwindank@gmail.com") {
        viewModelScope.launch {
            _googleUserEmail.value = email
            // Restoring custom setup for this email
            val currentProfile = userProfile.value ?: UserProfile()
            val restoredProfile = currentProfile.copy(
                googleAccountEmail = email,
                username = "Erwin",
                dataSourceType = "GOOGLE_SHEETS",
                sheetUrl = "1ydtkm3-P_37m1Opim0IS5WfIs2LS1VRx_D8fOL4kFVM",
                sheetTabName = "Full Raw Listening History",
                timeZone = "America/Guatemala (GMT-6)"
            )
            repository.saveUserProfile(restoredProfile)
        }
    }

    fun signOutGoogle() {
        _googleUserEmail.value = null
        viewModelScope.launch {
            val currentProfile = userProfile.value ?: UserProfile()
            repository.saveUserProfile(currentProfile.copy(googleAccountEmail = ""))
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun login(username: String) {
        viewModelScope.launch {
            repository.loginUser(username)
            _isLoggedIn.value = true
        }
    }

    fun register(username: String) {
        viewModelScope.launch {
            repository.registerUser(username)
            _isLoggedIn.value = true
        }
    }

    fun logout() {
        _isLoggedIn.value = false
    }
}
