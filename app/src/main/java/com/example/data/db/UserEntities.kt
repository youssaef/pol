package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "بطل المستقبل",
    val age: Int = 6,
    val languageCode: String = "ar",
    val selectedHeroId: String = "zaki",
    val selectedVehicleId: String = "scooter",
    val currentLevel: Int = 1,
    val currentXp: Int = 0,
    val coins: Int = 100,
    val stars: Int = 10,
    val totalPlaytimeSeconds: Long = 0L,
    val totalLearningTimeSeconds: Long = 0L,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val adaptiveScore: Float = 0.5f,
    val isTutorialCompleted: Boolean = false
)

@Entity(tableName = "unlocked_items")
data class UnlockedItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemType: String, // "world", "hero", "vehicle"
    val itemId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_missions")
data class DailyMissionEntity(
    @PrimaryKey val missionId: String,
    val titleAr: String,
    val titleFr: String,
    val titleEn: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardCoins: Int,
    val rewardXp: Int,
    val isClaimed: Boolean = false
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val titleAr: String,
    val titleFr: String,
    val titleEn: String,
    val iconEmoji: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val isUnlocked: Boolean = false,
    val rewardStars: Int
)

@Entity(tableName = "category_stats")
data class CategoryStatEntity(
    @PrimaryKey val categoryKey: String,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0
)
