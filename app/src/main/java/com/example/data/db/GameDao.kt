package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM unlocked_items")
    fun getUnlockedItems(): Flow<List<UnlockedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockItem(item: UnlockedItemEntity)

    @Query("SELECT * FROM daily_missions")
    fun getDailyMissions(): Flow<List<DailyMissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<DailyMissionEntity>)

    @Update
    suspend fun updateMission(mission: DailyMissionEntity)

    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("SELECT * FROM category_stats")
    fun getCategoryStats(): Flow<List<CategoryStatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCategoryStat(stat: CategoryStatEntity)
}
