package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.remote.GeminiRepository
import com.example.domain.model.AchievementEntity
import com.example.domain.model.ScanMode
import com.example.domain.model.ScanResultEntity
import com.example.domain.model.UserProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

sealed class ScanUiState {
    object Idle : ScanUiState()
    data class Processing(val step: String, val progress: Float) : ScanUiState()
    data class Success(val scanResult: ScanResultEntity) : ScanUiState()
    data class Error(val message: String) : ScanUiState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.scanResultDao()
    private val geminiRepository = GeminiRepository()

    val scansHistory: StateFlow<List<ScanResultEntity>> = dao.getAllScans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userProfile: StateFlow<UserProfileEntity?> = dao.getUserProfile()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserProfileEntity()
        )

    val achievements: StateFlow<List<AchievementEntity>> = dao.getAllAchievements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    private val _selectedMode = MutableStateFlow(ScanMode.FULL_ANALYSIS)
    val selectedMode: StateFlow<ScanMode> = _selectedMode.asStateFlow()

    private val _currentScanResult = MutableStateFlow<ScanResultEntity?>(null)
    val currentScanResult: StateFlow<ScanResultEntity?> = _currentScanResult.asStateFlow()

    private val _showDisclaimer = MutableStateFlow(false)
    val showDisclaimer: StateFlow<Boolean> = _showDisclaimer.asStateFlow()

    init {
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        viewModelScope.launch {
            val currentProfile = dao.getUserProfile().first()
            if (currentProfile == null) {
                dao.insertOrUpdateUserProfile(UserProfileEntity())
            }

            val currentAchievements = dao.getAllAchievements().first()
            if (currentAchievements.isEmpty()) {
                val defaults = listOf(
                    AchievementEntity("ach_first_scan", "First Neural Lens", "Scan your first selfie", "ic_scan", true, 1, 1),
                    AchievementEntity("ach_glowup", "Glow Up Master", "Achieve overall score above 85", "ic_star", true, 1, 1),
                    AchievementEntity("ach_roast", "Roast Survivor", "Survive a brutal AI Roast", "ic_fire", true, 1, 1),
                    AchievementEntity("ach_streak_5", "Daily Visionary", "Maintain a 5-day scan streak", "ic_lightning", false, 3, 5),
                    AchievementEntity("ach_social", "Viral Icon", "Share an AI report card", "ic_share", false, 0, 1)
                )
                dao.insertAchievements(defaults)
            }
        }
    }

    fun setSelectedMode(mode: ScanMode) {
        _selectedMode.value = mode
    }

    fun setShowDisclaimer(show: Boolean) {
        _showDisclaimer.value = show
    }

    fun resetScanState() {
        _scanUiState.value = ScanUiState.Idle
    }

    fun selectScanResult(scan: ScanResultEntity) {
        _currentScanResult.value = scan
    }

    fun processPhotoScan(bitmap: Bitmap) {
        viewModelScope.launch {
            val mode = _selectedMode.value
            _scanUiState.value = ScanUiState.Processing("Detecting facial geometry...", 0.2f)

            // Step 1: Save local image bitmap file
            val imagePath = saveBitmapToInternalStorage(bitmap)
            kotlinx.coroutines.delay(400)

            _scanUiState.value = ScanUiState.Processing("Validating photo symmetry & lighting...", 0.5f)
            kotlinx.coroutines.delay(500)

            _scanUiState.value = ScanUiState.Processing("Running Gemini AI neural analysis...", 0.8f)

            // Step 2: Gemini / Heuristic AI Analysis
            val result = geminiRepository.analyzeSelfie(bitmap, mode, imagePath)

            // Step 3: Insert into Room Database
            val insertedId = dao.insertScan(result)
            val savedEntity = result.copy(id = insertedId)

            _currentScanResult.value = savedEntity

            // Step 4: Update user credits & scan count
            val profile = dao.getUserProfile().first() ?: UserProfileEntity()
            if (profile.credits > 0 && !profile.isPremium) {
                dao.insertOrUpdateUserProfile(
                    profile.copy(
                        credits = profile.credits - 1,
                        totalScansDone = profile.totalScansDone + 1
                    )
                )
            } else {
                dao.insertOrUpdateUserProfile(
                    profile.copy(totalScansDone = profile.totalScansDone + 1)
                )
            }

            _scanUiState.value = ScanUiState.Success(savedEntity)
        }
    }

    fun toggleFavoriteScan(scan: ScanResultEntity) {
        viewModelScope.launch {
            val updated = scan.copy(isFavorite = !scan.isFavorite)
            dao.updateScan(updated)
            if (_currentScanResult.value?.id == scan.id) {
                _currentScanResult.value = updated
            }
        }
    }

    fun deleteScan(scanId: Long) {
        viewModelScope.launch {
            dao.deleteScanById(scanId)
            if (_currentScanResult.value?.id == scanId) {
                _currentScanResult.value = null
            }
        }
    }

    fun claimSpinReward() {
        viewModelScope.launch {
            val profile = dao.getUserProfile().first() ?: UserProfileEntity()
            dao.insertOrUpdateUserProfile(profile.copy(credits = profile.credits + 5))
        }
    }

    fun togglePremiumStatus() {
        viewModelScope.launch {
            val profile = dao.getUserProfile().first() ?: UserProfileEntity()
            dao.insertOrUpdateUserProfile(profile.copy(isPremium = !profile.isPremium))
        }
    }

    private fun saveBitmapToInternalStorage(bitmap: Bitmap): String {
        return try {
            val context = getApplication<Application>()
            val filename = "selfie_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, filename)
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
