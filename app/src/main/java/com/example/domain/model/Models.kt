package com.example.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ScanMode(val title: String, val description: String) {
    FULL_ANALYSIS("Full AI Analysis", "Comprehensive 12-point facial analysis & age estimate"),
    ROAST_MODE("AI Roast Mode", "Lighthearted, hilarious AI commentary on your selfie"),
    COMPLIMENT_MODE("AI Compliment Mode", "Wholesome AI praise & confidence booster"),
    CELEBRITY_LOOKALIKE("Celebrity Lookalike", "Find out which star share your facial features")
}

data class ScoreItem(
    val title: String,
    val score: Int, // 0 to 100
    val label: String,
    val description: String,
    val iconName: String
)

data class AiTips(
    val styleTip: String,
    val photoQualityTip: String,
    val groomingTip: String
)

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val imagePath: String? = null,
    val scanMode: String = ScanMode.FULL_ANALYSIS.name,
    val estimatedAge: Int,
    val ageRangeMin: Int,
    val ageRangeMax: Int,
    val smileScore: Int,
    val confidenceScore: Int,
    val styleScore: Int,
    val symmetryScore: Int,
    val beardScore: Int,
    val hairScore: Int,
    val skinScore: Int,
    val eyeContactScore: Int,
    val photoQualityScore: Int,
    val profileScore: Int,
    val overallScore: Int,
    val firstImpression: String,
    val aiRoastOrCompliment: String,
    val styleTip: String,
    val photoTip: String,
    val groomingTip: String,
    val isFavorite: Boolean = false
) {
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Alex Vance",
    val email: String = "alex.vance@example.com",
    val isPremium: Boolean = false,
    val credits: Int = 10,
    val streakDays: Int = 3,
    val lastScanDate: Long = System.currentTimeMillis(),
    val referralCode: String = "LENS-8821",
    val totalScansDone: Int = 5
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)
