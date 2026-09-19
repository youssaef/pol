package com.example.data.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max
import kotlin.math.min

class GameRepository(private val dao: GameDao) {

    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val unlockedItems: Flow<List<UnlockedItemEntity>> = dao.getUnlockedItems()
    val dailyMissions: Flow<List<DailyMissionEntity>> = dao.getDailyMissions()
    val achievements: Flow<List<AchievementEntity>> = dao.getAchievements()
    val categoryStats: Flow<List<CategoryStatEntity>> = dao.getCategoryStats()

    suspend fun initializeDefaultsIfNeeded() {
        val existingProfile = dao.getUserProfileOnce()
        if (existingProfile == null) {
            val starterProfile = UserProfileEntity(
                id = 1,
                playerName = "البطل الصغير",
                age = 6,
                languageCode = "ar",
                selectedHeroId = "zaki",
                selectedVehicleId = "scooter",
                currentLevel = 1,
                currentXp = 0,
                coins = 120,
                stars = 15,
                totalPlaytimeSeconds = 0L,
                totalLearningTimeSeconds = 0L,
                totalQuestionsAnswered = 0,
                totalCorrectAnswers = 0,
                adaptiveScore = 0.5f,
                isTutorialCompleted = false
            )
            dao.saveUserProfile(starterProfile)

            // Starter unlocked items
            dao.unlockItem(UnlockedItemEntity(itemType = "world", itemId = "alphabet"))
            dao.unlockItem(UnlockedItemEntity(itemType = "world", itemId = "words"))
            dao.unlockItem(UnlockedItemEntity(itemType = "hero", itemId = "zaki"))
            dao.unlockItem(UnlockedItemEntity(itemType = "hero", itemId = "sarah"))
            dao.unlockItem(UnlockedItemEntity(itemType = "vehicle", itemId = "scooter"))

            // Starter Daily Missions
            val missions = listOf(
                DailyMissionEntity(
                    missionId = "m_run",
                    titleAr = "أكمل جولة جري واحدة في أي عالم 🏃",
                    titleFr = "Termine une course dans n'importe quel monde 🏃",
                    titleEn = "Complete 1 run in any world 🏃",
                    targetCount = 1,
                    currentCount = 0,
                    rewardCoins = 50,
                    rewardXp = 100
                ),
                DailyMissionEntity(
                    missionId = "m_questions",
                    titleAr = "أجب عن 5 أسئلة تعليمية صحيحة 🧠",
                    titleFr = "Réponds à 5 questions éducatives 🧠",
                    titleEn = "Answer 5 educational questions correctly 🧠",
                    targetCount = 5,
                    currentCount = 0,
                    rewardCoins = 75,
                    rewardXp = 150
                ),
                DailyMissionEntity(
                    missionId = "m_coins",
                    titleAr = "اجمع 50 عملة ذهبية أثناء الجري 🪙",
                    titleFr = "Ramasse 50 pièces dorées pendant la course 🪙",
                    titleEn = "Collect 50 gold coins during runs 🪙",
                    targetCount = 50,
                    currentCount = 0,
                    rewardCoins = 60,
                    rewardXp = 120
                )
            )
            dao.insertMissions(missions)

            // Starter Achievements
            val initialAchievements = listOf(
                AchievementEntity(
                    achievementId = "ach_first_run",
                    titleAr = "الانطلاقة الأولى 🚀",
                    titleFr = "Premier Départ 🚀",
                    titleEn = "First Run 🚀",
                    iconEmoji = "👟",
                    targetCount = 1,
                    currentCount = 0,
                    isUnlocked = false,
                    rewardStars = 5
                ),
                AchievementEntity(
                    achievementId = "ach_score_1000",
                    titleAr = "حاجز الألف نقطة 🏆",
                    titleFr = "Cap des 1000 points 🏆",
                    titleEn = "1000 Points Milestone 🏆",
                    iconEmoji = "⭐",
                    targetCount = 1000,
                    currentCount = 0,
                    isUnlocked = false,
                    rewardStars = 10
                ),
                AchievementEntity(
                    achievementId = "ach_scholar_20",
                    titleAr = "العبقري الصغير (20 سؤال) 📚",
                    titleFr = "Petit Génie (20 questions) 📚",
                    titleEn = "Little Genius (20 Questions) 📚",
                    iconEmoji = "🎓",
                    targetCount = 20,
                    currentCount = 0,
                    isUnlocked = false,
                    rewardStars = 15
                ),
                AchievementEntity(
                    achievementId = "ach_algeria",
                    titleAr = "مستكشف الجزائر 🇩🇿",
                    titleFr = "Explorateur de l'Algérie 🇩🇿",
                    titleEn = "Algeria Explorer 🇩🇿",
                    iconEmoji = "🌴",
                    targetCount = 5,
                    currentCount = 0,
                    isUnlocked = false,
                    rewardStars = 10
                )
            )
            dao.insertAchievements(initialAchievements)
        }
    }

    suspend fun updateLanguage(code: String) {
        val p = dao.getUserProfileOnce() ?: return
        dao.saveUserProfile(p.copy(languageCode = code))
    }

    suspend fun updateAge(age: Int) {
        val p = dao.getUserProfileOnce() ?: return
        dao.saveUserProfile(p.copy(age = age))
    }

    suspend fun selectHero(heroId: String) {
        val p = dao.getUserProfileOnce() ?: return
        dao.saveUserProfile(p.copy(selectedHeroId = heroId))
    }

    suspend fun selectVehicle(vehicleId: String) {
        val p = dao.getUserProfileOnce() ?: return
        dao.saveUserProfile(p.copy(selectedVehicleId = vehicleId))
    }

    suspend fun setTutorialCompleted() {
        val p = dao.getUserProfileOnce() ?: return
        dao.saveUserProfile(p.copy(isTutorialCompleted = true))
    }

    suspend fun recordRunResults(
        coinsEarned: Int,
        starsEarned: Int,
        xpEarned: Int,
        runDurationSeconds: Long,
        learningSeconds: Long,
        questionsAnswered: Int,
        questionsCorrect: Int,
        scoreAchieved: Int,
        worldId: String
    ) {
        val p = dao.getUserProfileOnce() ?: return

        val newCoins = p.coins + coinsEarned
        val newStars = p.stars + starsEarned
        var newXp = p.currentXp + xpEarned
        var newLevel = p.currentLevel

        // XP formula for leveling up: each level requires level * 200 XP
        while (newXp >= newLevel * 200) {
            newXp -= newLevel * 200
            newLevel++
        }

        // Adaptive difficulty adjustment based on accuracy
        var newAdaptiveScore = p.adaptiveScore
        if (questionsAnswered > 0) {
            val sessionAccuracy = questionsCorrect.toFloat() / questionsAnswered.toFloat()
            newAdaptiveScore = if (sessionAccuracy >= 0.75f) {
                min(1.0f, p.adaptiveScore + 0.1f)
            } else if (sessionAccuracy <= 0.4f) {
                max(0.1f, p.adaptiveScore - 0.1f)
            } else {
                p.adaptiveScore
            }
        }

        dao.saveUserProfile(
            p.copy(
                coins = newCoins,
                stars = newStars,
                currentXp = newXp,
                currentLevel = newLevel,
                totalPlaytimeSeconds = p.totalPlaytimeSeconds + runDurationSeconds,
                totalLearningTimeSeconds = p.totalLearningTimeSeconds + learningSeconds,
                totalQuestionsAnswered = p.totalQuestionsAnswered + questionsAnswered,
                totalCorrectAnswers = p.totalCorrectAnswers + questionsCorrect,
                adaptiveScore = newAdaptiveScore
            )
        )

        // Progress Daily Missions
        val missions = dao.getDailyMissions().firstOrNull() ?: emptyList()
        for (m in missions) {
            if (!m.isClaimed) {
                var updatedCount = m.currentCount
                if (m.missionId == "m_run") updatedCount = min(m.targetCount, updatedCount + 1)
                if (m.missionId == "m_questions") updatedCount = min(m.targetCount, updatedCount + questionsCorrect)
                if (m.missionId == "m_coins") updatedCount = min(m.targetCount, updatedCount + coinsEarned)
                if (updatedCount != m.currentCount) {
                    dao.updateMission(m.copy(currentCount = updatedCount))
                }
            }
        }

        // Progress Achievements
        val achs = dao.getAchievements().firstOrNull() ?: emptyList()
        for (ach in achs) {
            if (!ach.isUnlocked) {
                var updated = ach.currentCount
                if (ach.achievementId == "ach_first_run") updated = min(ach.targetCount, updated + 1)
                if (ach.achievementId == "ach_score_1000") updated = max(updated, scoreAchieved)
                if (ach.achievementId == "ach_scholar_20") updated = min(ach.targetCount, updated + questionsCorrect)
                if (ach.achievementId == "ach_algeria" && worldId == "algeria") updated = min(ach.targetCount, updated + 1)

                val isNowUnlocked = updated >= ach.targetCount
                if (updated != ach.currentCount || isNowUnlocked) {
                    dao.updateAchievement(ach.copy(currentCount = updated, isUnlocked = isNowUnlocked))
                }
            }
        }

        // Progress Category Stats
        val existingCat = dao.getCategoryStats().firstOrNull()?.find { it.categoryKey == worldId }
        val updatedCat = CategoryStatEntity(
            categoryKey = worldId,
            totalQuestions = (existingCat?.totalQuestions ?: 0) + questionsAnswered,
            correctAnswers = (existingCat?.correctAnswers ?: 0) + questionsCorrect
        )
        dao.saveCategoryStat(updatedCat)
    }

    suspend fun claimMission(missionId: String) {
        val missions = dao.getDailyMissions().firstOrNull() ?: return
        val mission = missions.find { it.missionId == missionId } ?: return
        if (mission.currentCount >= mission.targetCount && !mission.isClaimed) {
            dao.updateMission(mission.copy(isClaimed = true))
            val p = dao.getUserProfileOnce() ?: return
            dao.saveUserProfile(p.copy(coins = p.coins + mission.rewardCoins, currentXp = p.currentXp + mission.rewardXp))
        }
    }

    suspend fun unlockNewWorld(worldId: String) {
        dao.unlockItem(UnlockedItemEntity(itemType = "world", itemId = worldId))
    }
}
