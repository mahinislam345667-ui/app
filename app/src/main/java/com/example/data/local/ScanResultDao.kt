package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.domain.model.AchievementEntity
import com.example.domain.model.ScanResultEntity
import com.example.domain.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<ScanResultEntity>>

    @Query("SELECT * FROM scan_results WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteScans(): Flow<List<ScanResultEntity>>

    @Query("SELECT * FROM scan_results WHERE id = :id")
    suspend fun getScanById(id: Long): ScanResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanResultEntity): Long

    @Update
    suspend fun updateScan(scan: ScanResultEntity)

    @Query("DELETE FROM scan_results WHERE id = :id")
    suspend fun deleteScanById(id: Long)

    @Query("DELETE FROM scan_results")
    suspend fun clearAllScans()

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserProfile(profile: UserProfileEntity)

    // Achievements
    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)
}
